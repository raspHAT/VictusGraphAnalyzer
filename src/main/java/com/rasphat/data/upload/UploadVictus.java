package com.rasphat.data.upload;

import net.lingala.zip4j.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UploadVictus extends Upload implements UploadProcessor {

    private final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    private final Logger logger = LoggerFactory.getLogger(UploadVictus.class);
    private final String NAME_OF_PROPERTY = "app."+UploadType.VICTUS.name();

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        extractZip(multipartFile, getPasswordFromProperty(NAME_OF_PROPERTY));

        System.out.println(TEMP_DIR_PATH);
        System.out.println("VICTUS PASSWORD: "+ getPasswordFromProperty(NAME_OF_PROPERTY));
        return null;
    }

    private void extractZip(MultipartFile multipartFile, String password) {
        try {
            //File tempZipFile = File.createTempFile("upload", ".zip");
            //multipartFile.transferTo(tempZipFile);

            // file is not
            if (!isValidZipFile((File) multipartFile)) {
                logger.info("Not a zip file");
                return;
            }

            ZipFile zipFile = new ZipFile((File) multipartFile);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(TEMP_DIR_PATH);

            // Clean up the temporary file
            System.out.println(((File) multipartFile).delete()+" File deleted!!!");
        } catch (IOException e) {
            logger.error("Error extracting zip file: " + e.getMessage());
        }
    }
}