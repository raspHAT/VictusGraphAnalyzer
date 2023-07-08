package com.rasphat.data.upload;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Abstract class for uploading and processing files.
 */
public abstract class Upload {

    protected static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    protected static final String FILENAME = "VictusGraphAnalyzer.zip";
    protected List<UploadData> uploadDataList = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Upload.class);
    protected String project;
    private static final String DATE_TIME_FORMAT = "M/d/yyyy h:mm:ss a";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final String DATE_TIME_REGEX = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}:\\d{2} [AP]M";
    private static final Pattern PATTERN = Pattern.compile(DATE_TIME_REGEX);
    protected static SimpleRegression simpleRegression = new SimpleRegression();



    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}.*?|" +
                    "\\w{3} \\w{3}.+\\d{1,2} \\d{2}:\\d{2}:\\d{2} UTC \\d{4}.*?|" +
                    "\\d{2}.\\d{2}.\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?|" +
                    "\\d{1,2}.\\d{1,2}.\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?"
    );

    /**
     * Retrieves the password from the application.properties file for the specified property.
     * The name of the property is given as a parameter.
     *
     * @param propertyName The name of the property for which the password is to be retrieved.
     * @return The password associated with the property in the application.properties file.
     * @throws RuntimeException If there's an IOException when loading application.properties.
     */
    protected String getPasswordFromProperty(String propertyName) {
        project = propertyName;
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
            return properties.getProperty("app." + propertyName);
        } catch (IOException e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
    }

    /**
     * Extracts the contents of a ZIP file from the given MultipartFile to a temporary directory.
     * Deletes the temporary directory if it already exists.
     *
     * @param multipartFile The MultipartFile containing the ZIP file.
     * @param password      The password for the ZIP file (if encrypted).
     */
    protected void extractZip(MultipartFile multipartFile, String password) {
        try {
            createTempDirectory();
            File tempFile = transferFile(multipartFile);

            if (!isValidZipFile(tempFile)) {
                LOGGER.info("Not a zip file");
                return;
            }

            try (ZipFile zipFile = new ZipFile(tempFile)) {
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password.toCharArray());
                }
                zipFile.extractAll(TEMP_DIR_PATH);
            }
        } catch (IOException e) {
            LOGGER.error("Error extracting zip file: " + e.getMessage());
        }
    }

    /**
     * Checks if the specified file is a valid ZIP file.
     * If the file is a valid ZIP file, this method also logs whether the ZIP file is encrypted.
     *
     * @param file The file to be checked.
     * @return True if the file is a valid ZIP file, false otherwise.
     * @throws IOException If an error occurs while trying to close the ZipFile.
     */
    protected boolean isValidZipFile(File file) throws IOException {
        try (ZipFile ignored = new ZipFile(file)) {
            return true; // valid if no exception is thrown
        } catch (ZipException e) {
            LOGGER.error(file + " is not a valid ZIP file. Exception message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates the temporary directory if it doesn't exist.
     *
     * @throws IOException If an I/O error occurs during directory creation.
     */
    protected void createTempDirectory() throws IOException {
        File tempDirectory = new File(TEMP_DIR_PATH);
        LOGGER.info(tempDirectory.getAbsolutePath());
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                LOGGER.error("Failed to create directory: " + TEMP_DIR_PATH);
            }
        }
    }

    /**
     * Transfers the MultipartFile to a temporary file.
     *
     * @param multipartFile The MultipartFile to transfer.
     * @return The temporary File object representing the transferred file.
     * @throws IOException If an I/O error occurs during the file transfer.
     */
    protected File transferFile(MultipartFile multipartFile) throws IOException {
        File tempFile = new File(TEMP_DIR_PATH, FILENAME);
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    /**
     * Processes files from a specific directory, adding the contents to a list of UploadData objects.
     * <p>
     * This method first clears the current list of UploadData objects to prevent duplication.
     * It then attempts to process a directory, specified by the TEMP_DIR_PATH, adding each file's data
     * to the list. If an IOException occurs during this process, an error message is logged and
     * the exception is propagated upwards.
     * </p>
     *
     * @return a list of UploadData objects, each representing data from a single file
     * @throws IOException if there's an error reading the directory or any file within it
     */
    protected List<UploadData> processFiles() throws IOException {
        uploadDataList.clear();  // Clear the list to avoid adding duplicate data
        try {
            processDirectory(new File(TEMP_DIR_PATH), uploadDataList);

        } catch (IOException e) {
            LOGGER.error("ProcessFiles: {}",e.getMessage());
        }
        return uploadDataList;
    }

    /**
     * Recursively processes the files in the given directory and adds the UploadData objects to the provided list.
     *
     * @param directory        The directory to process.
     * @param uploadDataList   The list to add the UploadData objects to.
     * @throws IOException     If an I/O error occurs.
     */
    protected void processDirectory(File directory, List<UploadData> uploadDataList) throws IOException {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    processFile(file, uploadDataList);
                } else if (file.isDirectory()) {
                    processDirectory(file, uploadDataList);
                }
            }
        }
    }

    /**
     * Processes a file and creates an UploadData object for each line in the file.
     * If the file's name matches any of the ignored names (as determined by isIgnoredFile),
     * the method does not process the file.
     *
     * @param file The file to process.
     * @param uploadDataList The list to add the UploadData objects to.
     * @throws IOException If an I/O error occurs.
     */
    protected void processFile(File file, List<UploadData> uploadDataList) throws IOException {
        String filename = file.getName();
        if (!isIgnoredFile(filename)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LocalDateTime localDateTime = findDateTimeInString(line);
                    uploadDataList.add(new UploadData(filename, line, project, localDateTime));
                }
            }
        }
    }

    /**
     * Checks if the given filename is one of the ignored filenames.
     * The ignored filenames contain "VictusGraphAnalyzer.zip", "Screenshot.png",  and any filename containing "crash".
     *
     * @param filename The filename to check.
     * @return True if the filename is ignored, false otherwise.
     */
    private boolean isIgnoredFile(String filename) {
        return filename.equals("VictusGraphAnalyzer.zip")
                || filename.contains("Screenshot.png")
                || filename.contains("crash")
                || filename.contains("WebDiag.html")
                || filename.contains(".DS_Store")
                || filename.contains("Exception.txt")
                || filename.contains("ToolBox")
                || filename.contains(".xml");
    }

    /**
     * Extracts the first date time string that matches a predefined set of formats from the given input string.
     * This method applies a regular expression pattern to the input string to find any sequences of characters
     * that match any of the predefined date time formats. If it finds a matching sequence, it parses the sequence
     * into a LocalDateTime object and returns it.
     * If no matching sequence is found, or if a found sequence cannot be parsed into a LocalDateTime object,
     * this method throws a DateTimeParseException.
     *
     * @param input the string to search for a date time sequence
     * @return the first LocalDateTime object found in the input string
     * @throws DateTimeParseException if no date time sequence is found in the input string, or if a found sequence
     *                                cannot be parsed into a LocalDateTime object
     */
    public LocalDateTime findDateTimeInString(String input) throws DateTimeParseException {
        Matcher matcher = DATE_PATTERN.matcher(input);
        if (matcher.find()) {
            String matchedDate = matcher.group();
            return parseDateTime(matchedDate);
        }
        throw new DateTimeParseException("Upload.findDateTimeInString(String input): No date in String fund: {}", input, 0);
    }

    /**
     * Attempts to parse the given string into a LocalDateTime object by applying various date formats.
     * This method attempts to transform the input string into a LocalDateTime object by iterating through various
     * predefined date and time formats. It returns the first successfully created LocalDateTime object.
     * If none of the predefined formats can successfully transform the input string into a LocalDateTime object,
     * this method throws a DateTimeParseException.
     *
     * @param input the string to be parsed
     * @return the LocalDateTime object that was created from the input string
     * @throws DateTimeParseException if the input string cannot be transformed into a LocalDateTime object
     */
    protected LocalDateTime parseDateTime(String input) throws DateTimeParseException {
        List<String> FORMATS = Arrays.asList(
                "yyyy-MM-dd'T'HH:mm:ss.SSS",        // ASC Zeit
                //"EEE MMM d HH:mm:ss zzz yyyy",      // WEBDIAG Zeit
                "M/d/yyyy HH:mm:ss.SSS"          // GUI LOGS
                //"M/d/yyyy HH:mm:ss.SSS"    // & OCT Logs
        );

        for (String format : FORMATS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                LOGGER.error("parseDateTime, {} Error {}", format, e.getMessage());
            }
        }
        throw new DateTimeParseException("No format match in inputString found: {}", input, 0);
    }

    /**
     * Deletes the temporary directory and its contents if it exists.
     */
    protected void deleteTempDirectory() {
        File tempDirectory = new File(TEMP_DIR_PATH);

        if (!tempDirectory.exists()) {
            // Directory doesn't exist, nothing to delete
            return;
        }

        try {
            FileUtils.deleteDirectory(tempDirectory);
            LOGGER.info("Temporary directory deleted: " + TEMP_DIR_PATH);
        } catch (IOException e) {
            LOGGER.error("Error deleting temporary directory: " + e.getMessage());
        }
    }
}