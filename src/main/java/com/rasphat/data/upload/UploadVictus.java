package com.rasphat.data.upload;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class UploadVictus extends Upload implements UploadProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UploadVictus.class);
    private final String NAME_OF_PROPERTY = "app."+UploadType.VICTUS.name();

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        extractZip(multipartFile, password);

        System.out.println(TEMP_DIR_PATH);
        System.out.println("VICTUS PASSWORD: "+ password);
        return null;
    }

    public void extractZip(MultipartFile multipartFile, String password) {
        try {
            File tempZipFile = File.createTempFile("upload", ".zip");
            multipartFile.transferTo(tempZipFile);

            if (!isZipFile(tempZipFile)) {
                logger.info("File is null or is not a zipfile");
                return;
            }

            ZipFile zipFile = new ZipFile(tempZipFile);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(TEMP_DIR_PATH);

            // Clean up the temporary file
            System.out.println(tempZipFile.delete());
        } catch (IOException e) {
            logger.error("Error extracting zip file: " + e.getMessage());
        }
    }
}