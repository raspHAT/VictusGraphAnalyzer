package com.rasphat.data.upload;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.exception.ZipException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Abstract class for uploading and processing files.
 */
public abstract class Upload {

    protected final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    protected List<UploadData> uploadDataList = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(Upload.class);
    private String project = "";

    /**
     * Retrieves the password from the application.properties file based on the given property name.
     *
     * @param nameOfProperty The name of the property to retrieve.
     * @return The value of the property.
     */
    protected String getPasswordFromProperty(String nameOfProperty) {
        project = nameOfProperty;
        String password;
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
            password = properties.getProperty(nameOfProperty);
        } catch (IOException e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        return password;
    }

    /**
     * Checks whether the provided file is a valid ZIP file.
     *
     * @param file The file to check.
     * @return True if the file is a valid ZIP file, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    protected boolean isValidZipFile(File file) throws IOException {
        try (ZipFile zipFile = new ZipFile(file)) {
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();

            for (FileHeader fileHeader : fileHeaders) {
                if (fileHeader.isEncrypted()) {
                    logger.debug(fileHeader.getFileName() + " zip file is encrypted.");
                } else {
                    logger.debug(fileHeader.getFileName() + " zip file is not encrypted.");
                }
            }

            // If we got to this point without an exception being thrown, the file is a valid ZIP file
            return true;
        } catch (ZipException ex) {
            logger.error(file + " is not a valid ZIP file. Exception message: " + ex.getMessage());
        }

        // If an exception was thrown, the file is not a valid ZIP file
        return false;
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
        System.out.println(tempDirectory);
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

    /**
     * Processes the files in the temporary directory and returns a list of UploadData objects.
     *
     * @return The list of UploadData objects.
     * @throws IOException If an I/O error occurs.
     */
    protected List<UploadData> processFiles() throws IOException {
        List<UploadData> uploadDataList = new ArrayList<>();
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
     *
     * @param file             The file to process.
     * @param uploadDataList   The list to add the UploadData objects to.
     * @throws IOException     If an I/O error occurs.
     */
    protected void processFile(File file, List<UploadData> uploadDataList) throws IOException {

        String filename = file.getName();

        if (filename.equals("VictusGraphAnalyzer.zip")) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                UploadData uploadData = new UploadData(filename, line, project);
                uploadDataList.add(uploadData);
            }
        }
    }
}