package com.rasphat.data.upload;

import com.rasphat.data.portfolio.DateParser;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Abstract class for uploading and processing files.
 */
public abstract class Upload {

    protected static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    protected List<UploadData> uploadDataList = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(Upload.class);
    protected String project;

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
            logger.error(file + " is not a valid ZIP file. Exception message: " + ex.getMessage());
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
                logger.info("Not a zip file");
                return;
            }

            try (ZipFile zipFile = new ZipFile(tempFile)) {
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password.toCharArray());
                }
                zipFile.extractAll(TEMP_DIR_PATH);
            }
        } catch (IOException e) {
            logger.error("Error extracting zip file: " + e.getMessage());
        }
    }

    /**
     * Creates the temporary directory if it doesn't exist.
     *
     * @throws IOException If an I/O error occurs during directory creation.
     */
    protected void createTempDirectory() throws IOException {
        File tempDirectory = new File(TEMP_DIR_PATH);
        logger.info(tempDirectory.getAbsolutePath());
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                logger.error("Failed to create directory: " + TEMP_DIR_PATH);
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
            logger.info("Temporary directory deleted: " + TEMP_DIR_PATH);
        } catch (IOException e) {
            logger.error("Error deleting temporary directory: " + e.getMessage());
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
                    uploadDataList.add(new UploadData(filename, line, project, localDateTime, null, null));
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
}