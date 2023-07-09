package com.rasphat.data.upload;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Abstract class for uploading and processing files.
 */
public abstract class Upload {

    private static final Logger LOGGER = LoggerFactory.getLogger(Upload.class);
    private static final String FILENAME = "VictusGraphAnalyzer.zip";
    private static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    private static String project = "";

    protected static SimpleRegression simpleRegression = new SimpleRegression();
    protected static List<UploadData> uploadDataList = new ArrayList<>();

    public static void setProject(String project) {
        Upload.project = project;
    }

    /**
     * Retrieves the password from the application.properties file for the specified property.
     * The name of the property is given as a parameter.
     *
     * @param propertyName The name of the property for which the password is to be retrieved.
     * @return The password associated with the property in the application.properties file.
     * @throws RuntimeException If there's an IOException when loading application.properties.
     */
    protected String getPasswordFromProperty(String propertyName) {
        setProject(propertyName);
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
            return properties.getProperty("app." + propertyName);
        } catch (IOException e) {
            throw new RuntimeException("Upload.getPasswordFromProperty(): Failed to load application properties", e);
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

            // Checks if the specified file is a valid ZIP file.
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
        if (fileToLoad(filename)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (filename.contains("Shell")
                            || filename.contains("Sword")
                            || filename.contains("messages")
                            || filename.equals("HE2SOCT.log")
                    ) {
                        LocalDateTime localDateTime = UploadParser.findDateTimeInString(file, line);
                        System.out.println("Jaba Daba Doo" + localDateTime);
                        uploadDataList.add(new UploadData(filename, line, project, localDateTime));
                    } else {
                        System.out.println("Heutido" + file.getAbsolutePath());
                        uploadDataList.add(new UploadData(filename, line, project, LocalDateTime.now()));
                    }
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
    private boolean fileToLoad(String filename) {
        return     filename.contains("Shell")
                || filename.contains("Sword")
                || filename.contains("messages")
                || filename.equals("HE2SOCT.log")
                || filename.equals("Machine.xml")
                || filename.equals("Sword.xml")
                || filename.equals("SwordTesting.xml")
                || filename.equals("SystemTest.xml")
                || filename.equals("Toolbox.xml")
                || filename.equals("WebDiag.html");
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