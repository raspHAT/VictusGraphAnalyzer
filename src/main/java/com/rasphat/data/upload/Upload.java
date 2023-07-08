package com.rasphat.data.upload;

import com.rasphat.data.portfolio.DateParser;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Abstract class for uploading and processing files.
 */
public abstract class Upload {

    protected static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    protected List<UploadData> uploadDataList = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Upload.class);
    protected String project;
    private static final String DATE_TIME_FORMAT = "M/d/yyyy h:mm:ss a";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final String DATE_TIME_REGEX = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}:\\d{2} [AP]M";
    private static final Pattern PATTERN = Pattern.compile(DATE_TIME_REGEX);
    protected static SimpleRegression simpleRegression = new SimpleRegression();

    /**
     * Retrieves the password from the application.properties file for the specified property.
     * The name of the property is given as a parameter.
     *
     * @param propertyName The name of the property for which the password is to be retrieved.
     * @return The password associated with the property in the application.properties file.
     * @throws RuntimeException If there's an IOException when loading application.properties.
     */
    protected String getPasswordFromProperty(String propertyName) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
            return properties.getProperty(propertyName);
        } catch (IOException e) {
            throw new RuntimeException("Could not load application.properties", e);
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
        } catch (ZipException ex) {
            LOGGER.error(file + " is not a valid ZIP file. Exception message: " + ex.getMessage());
            return false;
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
        String ZIP_FILE_NAME = "VictusGraphAnalyzer.zip";
        File tempFile = new File(TEMP_DIR_PATH, ZIP_FILE_NAME);
        multipartFile.transferTo(tempFile);
        return tempFile;
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
    protected List<UploadData> processFiles() throws IOException {
        uploadDataList.clear();  // Clear the list to avoid adding duplicate data
        processDirectory(new File(TEMP_DIR_PATH), uploadDataList);
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
                    LocalDateTime localDateTime = DateParser.findDateTimeInString(line);
                    uploadDataList.add(new UploadData(filename, line, project, localDateTime));
                }
            }
        }
    }

    /**
     * Checks if the given filename is one of the ignored filenames.
     * The ignored filenames are "VictusGraphAnalyzer.zip", "Screenshot.png", "Exception.txt", "WebDiag.html", and any filename containing ".xml".
     *
     * @param filename The filename to check.
     * @return True if the filename is ignored, false otherwise.
     */
    private boolean isIgnoredFile(String filename) {
        return filename.equals("VictusGraphAnalyzer.zip")
                || filename.contains("Screenshot.png");
    }




    protected void calculateRegression(Map<LocalDateTime, Duration> timestampsDurationsMap) {

        // Check if map is null or empty
        if (timestampsDurationsMap == null || timestampsDurationsMap.isEmpty()) {
            throw new IllegalArgumentException("Invalid input map");
        }

        SimpleRegression regression = new SimpleRegression();

        for (Map.Entry<LocalDateTime, Duration> entry : timestampsDurationsMap.entrySet()) {
            double timestampAsEpochSecond = entry.getKey().toEpochSecond(ZoneOffset.UTC);
            double durationAsSeconds = entry.getValue().getSeconds();

            regression.addData(timestampAsEpochSecond, durationAsSeconds);
        }
        simpleRegression = regression;
        //return regression;
    }




    public Map<LocalDateTime, Duration> processUploadDataList(List<UploadData> uploadDataList) {
        // Filter the list for filenames containing "DMS"
        List<UploadData> filteredList = uploadDataList.stream()
                .filter(uploadData -> uploadData.getFilename().contains("DMS"))
                .collect(Collectors.toList());

        // Map to hold LocalDateTime as key and Duration as value
        Map<LocalDateTime, Duration> dateTimeDurationMap = new HashMap<>();

        for (UploadData uploadData : filteredList) {
            LocalDateTime fromRawLine = extractLocalDateTimeFromRawLine(uploadData.getRawLine()); // Your method to extract LocalDateTime from rawLine
            LocalDateTime fromUploadData = uploadData.getLocalDateTime();

            // Calculating the duration between the two dates
            Duration duration = Duration.between(fromRawLine, fromUploadData);

            // Putting the LocalDateTime and Duration into the map
            dateTimeDurationMap.put(fromUploadData, duration);
        }

        return dateTimeDurationMap;
    }






    private LocalDateTime extractLocalDateTimeFromRawLine(String rawLine) {
        // TODO: Implement your logic to extract LocalDateTime from rawLine

        if (rawLine != null) {

            // Find the date time substring using the pattern
            Matcher matcher = PATTERN.matcher(rawLine);
            if (matcher.find()) {
                String dateTimeString = matcher.group();

                return LocalDateTime.parse(dateTimeString, FORMATTER);
            }
        }
        return null;
    }

    public LocalDateTime correctDateTime(LocalDateTime dateTime) {
        // Convert the original LocalDateTime to epoch milliseconds
        long originalMilliseconds = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        // Predict the offset in milliseconds using SimpleRegression
        long offsetMilliseconds = (long)simpleRegression.predict(originalMilliseconds);

        // Correct the original LocalDateTime by the predicted offset and return
        return dateTime.minus(Duration.ofMillis(offsetMilliseconds));
    }
}