package com.rasphat.data.upload;

import com.rasphat.Main;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class Upload {

    private static final Logger logger = LoggerFactory.getLogger(Upload.class);
    private final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;


    public static UploadProcessor getUploadProcessor(String project) throws Exception {
        try {
            if (project.equals(UploadType.VICTUS.name())) {
                return new UploadVictus();
            } else if (project.equals(UploadType.TENEO.name())) {
                return new UploadTeneo();
            } else if (project.equals(UploadType.ZDW3.name())) {
                return new UploadZdw3();
            } else if (project.equals(UploadType.TENEO_TREATMENTS.name())) {
                return new UploadTeneoTreatments();
            } else {
                logger.debug("Unsupported project: " + project);
                return new UploadProjectUnknown(); // or you can throw an exception here
            }
        } catch (Exception e) { // You need to specify what kind of Exception you're catching
            logger.error("Error processing upload for project " + project, e);
            throw new Exception("Error processing upload for project " + project, e); // or you can rethrow the exception depending on your use case
        }
    }

    protected String getPasswordFromProperty(String nameOfProperty) {
        String password;
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
            password = properties.getProperty(nameOfProperty);
        } catch (IOException e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        return password;
    }

    /**
     * This function checks whether the provided file is a valid ZIP file.
     *
     * It tries to read the file using Zip4j library. During the reading process,
     * it iterates over each entry in the ZIP file. If the entry is encrypted,
     * it logs a debug message indicating that the file is encrypted. If the entry
     * is not encrypted, it logs a message indicating that it is not encrypted.
     *
     * If the function is able to read all entries in the ZIP file without any errors,
     * it concludes that the file is a valid ZIP file and returns true.
     *
     * If it encounters an error during the reading process (a ZipException is thrown),
     * it logs a debug message indicating that the file is not a valid ZIP file and returns false.
     *
     * @param file - The file to check.
     * @return boolean - true if the file is a valid ZIP file, false otherwise.
     */
    protected boolean isValidZipFile(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();

            for(FileHeader fileHeader : fileHeaders) {
                if (fileHeader.isEncrypted()) {
                    logger.debug(fileHeader.getFileName() + " zip file is encrypted.");
                } else {
                    logger.debug(fileHeader.getFileName() + " zip file is not encrypted.");
                }
            }
            // If we got to this point without an exception being thrown, the file is a valid ZIP file
            return true;
        } catch (ZipException ex) {
            logger.debug(file + " is not a valid ZIP file.");
        }
        // If an exception was thrown, the file is not a valid ZIP file
        return false;
    }
}