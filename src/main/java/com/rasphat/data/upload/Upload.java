package com.rasphat.data.upload;

import net.lingala.zip4j.ZipFile;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Abstract class for uploading and processing files.
 */
public abstract class Upload {

    private static final Logger LOGGER = LoggerFactory.getLogger(Upload.class);
    private static final List<UploadData> uploadDataList = new ArrayList<>();
    private String project = "TO_BE_SET";
    private String multiFileName = project + "_GraphAnalyzer.zip";

    public void setProject(String project) {
        this.project = project;
        multiFileName = project + "_GraphAnalyzer.zip";
    }

    protected String getProject() {
        return project;
    }

    protected String getMultiFileName() {
        return multiFileName;
    }

    public static List<UploadData> getUploadDataList() {
        return uploadDataList;
    }

    /**
     * Retrieves the password from the application.properties file for the specified property.
     *
     * @param propertyName The name of the property for which the password is to be retrieved.
     * @return The password associated with the property in the application.properties file, or a default value if not found.
     * @throws RuntimeException If there's an IOException when loading application.properties.
     */
    protected String getPasswordFromProperty(String propertyName) {
        setProject(propertyName);
        Properties properties = new Properties();
        try {
            ClassLoader classLoader = Upload.class.getClassLoader();
            try (InputStream input = classLoader.getResourceAsStream("application.properties")) {
                if (input != null) {
                    properties.load(input);
                    return properties.getProperty("app." + propertyName);
                }
            }
        } catch (IOException e) {
            advanceException(e);
        }
        LOGGER.warn("Failed to load application properties or property not found: {}", propertyName);
        return "defaultPassword";
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
            File tempFile = transferFile(multipartFile);

            // Checks if the specified file is a valid ZIP file.
            try (ZipFile zipFile = new ZipFile(tempFile)) {
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password.toCharArray());
                }
                zipFile.extractAll(UploadConstants.TEMP_DIR_PATH);
            }
        } catch (IOException e) {
            advanceException(e);
        }
    }

    /**
     * Creates the temporary directory if it doesn't exist.
     */
    protected void createTempDirectory() {
        Path tempDirectoryPath = Paths.get(UploadConstants.TEMP_DIR_PATH);
        LOGGER.info(tempDirectoryPath.toAbsolutePath().toString());
        registerShutdownHook(tempDirectoryPath.toFile());
        try {
            if (Files.notExists(tempDirectoryPath)) {
                Files.createDirectories(tempDirectoryPath);
            }
        } catch (IOException e) {
            advanceException(e);
        }
    }

    /**
     * Registers a shutdown hook that attempts to delete a specified file or directory
     * during program shutdown.
     *
     * @param file The file or directory to be deleted during shutdown.
     */
    protected void registerShutdownHook(File file) {
        LOGGER.info("Shutdown hook: {}", file);
        Thread shutdownThread = new Thread(() -> {
            try {
                LOGGER.info("Shutdown program, attempting to delete all temporary files.");
                LOGGER.info("Consider the deletion successful if no errors are reported after this log message!");
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                LOGGER.error("Error deleting the temporary file: {}", e.getMessage());
            }
        });
        shutdownThread.setName("shutdown-hook-thread"); // Set the name of the shutdown thread
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Transfers the MultipartFile to a temporary file.
     *
     * @param multipartFile The MultipartFile to transfer.
     * @return The temporary File object representing the transferred file.
     */
    protected File transferFile(MultipartFile multipartFile) {
        File tempFile = new File(UploadConstants.TEMP_DIR_PATH + getMultiFileName());
        try {
            //tempFile = new File(UploadConstants.TEMP_DIR_PATH + getMultiFileName());
            multipartFile.transferTo(tempFile);
            return tempFile;
        } catch (IOException e) {
            advanceException(e);
            return tempFile;
        }
    }

    /**
     * Processes files from a specific directory, adding the contents to a list of UploadData objects.
     * <p>
     * This method first clears the current list of UploadData objects to prevent duplication.
     * It then attempts to process a directory, specified by the TEMP_DIR_PATH, adding each file's data
     * to the list. If an IOException occurs during this process, an error message is logged and
     * the exception is propagated upwards.
     * </p>
     */
    protected void processFiles() {
        uploadDataList.clear();  // Clear the list to avoid adding duplicate data
        loadFilesToUploadDataList(Paths.get(UploadConstants.TEMP_DIR_PATH));
    }

    /**
     * Recursively processes the files in the given path and adds the UploadData objects to the provided list.
     *
     * @param path The path to process.
     */
    protected void loadFilesToUploadDataList(Path path) {
        try {
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .forEach(this::processFile);
        } catch (IOException e) {
            LOGGER.error(String.valueOf(path.getFileName()));
            advanceException(e);
        }
    }

    /**
     * Processes a path and creates an UploadData object for each line in the path.
     * If the path's name matches any of the ignored names (as determined by isIgnoredFile),
     * the method does not process the path.
     *
     * @param path The path to process.
     */
    protected void processFile(Path path) {

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                uploadDataList.add(new UploadData(path.toFile().getName(), line, project, null));
            }
        } catch (IOException e) {
            LOGGER.error(String.valueOf(path.getFileName()));
            advanceException(e);
        }
    }

    /**
     *
     * Helper method to advance and log an exception with the appropriate class, method, and line information.
     * This method is used internally within the Upload class to handle exceptions and log error messages.
     */
    protected void advanceException(Exception e) {
        LOGGER.error("Exception in {}, method: {}(), line: {}",
                getClass().getSimpleName(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                Thread.currentThread().getStackTrace()[1].getLineNumber()
        );
        LOGGER.error("getMessage(): {}", e.getMessage());
    }
}