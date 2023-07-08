package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the uploading of data specific to the 'Victus' upload type.
 * Extends the abstract class 'Upload' and implements 'UploadProcessor' interface.
 */
public class UploadVictus extends Upload implements UploadProcessor {

    private static final String NAME_OF_PROPERTY = "app."+UploadType.VICTUS.name();
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadVictus.class);

    /**
     * Process the upload data.
     * This method receives a MultipartFile as input and process it by extracting,
     * loading and sorting the data. It also handles exceptions and logs important information.
     *
     * @param multipartFile The data to upload.
     * @return A list of UploadData objects representing the processed data.
     */
    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        LOGGER.info("Processing upload data for {}", UploadVictus.class.getName());

        // Extracts zip file content using provided password
        extractZip(multipartFile, getPasswordFromProperty(NAME_OF_PROPERTY));

        try {
            // Process extracted files and stores in uploadDataList
            uploadDataList = processFiles();
        } catch (IOException e) {
            LOGGER.error("An error occurred while processing files.", e);
        }

        // Perform regression analysis on the data
        calculateRegression(processUploadDataList(uploadDataList));

        // Adjust the datetime for specific lines of data
        uploadDataList
                .forEach(data -> {
                    if (data.getFilename().contains("messages") && data.getRawLine().contains("asc28")) {
                        data.setLocalDateTime(correctDateTime(data.getLocalDateTime()));
                    }
                });

        // Sort the data by datetime
        uploadDataList.sort(Comparator.comparing(UploadData::getLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder())));

        // Return the processed list of data
        return uploadDataList;
    }
}
