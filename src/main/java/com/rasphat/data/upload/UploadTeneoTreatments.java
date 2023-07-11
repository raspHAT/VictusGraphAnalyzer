package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class UploadTeneoTreatments extends Upload implements UploadProcessor {

    private static final String NAME_OF_PROPERTY = UploadType.TENEO_TREATMENTS.name();
    private final Logger LOGGER = LoggerFactory.getLogger(UploadTeneoTreatments.class);

    @Override
    public void processUploadData(MultipartFile multipartFile) {

        LOGGER.info("Processing upload data from: {}", getClass());

        // Extracts zip file content using the provided property password
        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        // Create the temp folder with a shutdown hook
        createTempDirectory();

        extractZip(multipartFile, password);

        // Process extracted files and store in uploadDataList
        processFiles();
    }
}