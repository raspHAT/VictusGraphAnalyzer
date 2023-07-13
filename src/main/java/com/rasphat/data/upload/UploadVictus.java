package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;

/**
 * Handles the uploading of data specific to the 'Victus' upload type.
 * Extends the abstract class 'Upload' and implements the 'UploadProcessor' interface.
 */
public class UploadVictus extends Upload implements UploadProcessor {

    private static final String NAME_OF_PROPERTY = UploadType.VICTUS.name();
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadVictus.class);

    /**
     * Processes the upload data specific to the 'Victus' upload type.
     *
     * @param multipartFile The data to upload.
     */
    @Override
    public void processUploadData(MultipartFile multipartFile) {
        LOGGER.info("Processing upload data from: {}", getClass());

        // Get password from application.properties file
        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        // Create the temp folder with a shutdown hook
        createTempDirectory();

        // Extracts zip file content using the provided property password
        extractZip(multipartFile, password);

        // Process extracted files and store in uploadDataList
        processFiles();

        // parse LocalDateTime from rawlines due to logfile type
        UploadVictusParser uploadVictusParser = new UploadVictusParser();
        uploadVictusParser.ascTimeFromRawline(getUploadDataList());
        uploadVictusParser.guiTimeFromRawline(getUploadDataList());
        uploadVictusParser.octTimeFromRawline(getUploadDataList());
        uploadVictusParser.nullTimeFromRawline(getUploadDataList());

        // Perform regression analysis on the data
        Map<LocalDateTime, Duration> dateTimeDurationMap = uploadVictusParser.processUploadDataList(getUploadDataList());
        uploadVictusParser.populateTimeDurationMap(dateTimeDurationMap);

        // Correct the LocalDateTime for specific filenames using regression analysis
        getUploadDataList().stream()
                .filter(data -> data.getFilename().contains("message"))
                .forEach(data -> {
                    LocalDateTime originalDateTime = data.getLocalDateTime();
                    LocalDateTime correctedDateTime = uploadVictusParser.correctDateTime(originalDateTime);
                    data.setLocalDateTime(correctedDateTime);
                });

        // Sort the data by datetime
        getUploadDataList().sort(Comparator.comparing(UploadData::getLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder())));
    }
}