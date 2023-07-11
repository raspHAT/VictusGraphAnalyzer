package com.rasphat.data.upload;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Extracts zip file content using the provided property password
        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        // Create the temp folder with a shutdown hook
        createTempDirectory();

        extractZip(multipartFile, password);

        // Process extracted files and store in uploadDataList
        processFiles();

        // Perform regression analysis on the data
        // calculateRegression(processUploadDataList(uploadDataList));

        // Sort the data by datetime
        // uploadDataList.sort(Comparator.comparing(UploadData::getLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder())));

    }

    /**
     * Processes a list of UploadData objects, extracts "DMS" filenames, calculates the duration between
     * the rawLine LocalDateTime and the UploadData LocalDateTime, and stores the result in a map.
     *
     * @param uploadDataList The list of UploadData objects to process.
     * @return A map with LocalDateTime keys and Duration values. Each entry represents a LocalDateTime and the
     *         duration between it and another LocalDateTime extracted from the same UploadData object.
     */
    public static Map<LocalDateTime, Duration> processUploadDataList(List<UploadData> uploadDataList) {
        // Filter the list for filenames containing "DMS"
        List<UploadData> filteredList = uploadDataList.stream()
                .filter(uploadData -> uploadData.getFilename().contains("DMS"))
                .collect(Collectors.toList());

        // Map to hold LocalDateTime as key and Duration as value
        Map<LocalDateTime, Duration> dateTimeDurationMap = new HashMap<>();

        for (UploadData uploadData : filteredList) {
            // Your method to extract LocalDateTime from rawLine
            LocalDateTime fromRawLine = UploadParser.extractLocalDateTimeFromRawLine(uploadData.getRawLine());
            LocalDateTime fromUploadData = uploadData.getLocalDateTime();

            // Calculating the duration between the two dates
            Duration duration = Duration.between(fromRawLine, fromUploadData);

            // Putting the LocalDateTime and Duration into the map
            dateTimeDurationMap.put(fromUploadData, duration);
        }

        return dateTimeDurationMap;
    }

    /**
     * Performs simple linear regression on a provided map that has LocalDateTime as keys and Duration as values.
     *
     * @param timestampsDurationsMap The map with LocalDateTime keys and Duration values.
     *                               Each entry represents a timestamp and the duration associated with it.
     * @throws IllegalArgumentException If the input map is null or empty.
     */
    protected void calculateRegression(Map<LocalDateTime, Duration> timestampsDurationsMap) {
        // Check if map is null or empty
        if (timestampsDurationsMap == null || timestampsDurationsMap.isEmpty()) {
            throw new IllegalArgumentException("Invalid input map");
        }

        SimpleRegression regression = new SimpleRegression();

        for (Map.Entry<LocalDateTime, Duration> entry : timestampsDurationsMap.entrySet()) {
            double timestampAsEpochSecond = entry.getKey().toEpochSecond(ZoneOffset.UTC);
            double durationAsSeconds = entry.getValue().getSeconds();

            regression.addData(timestampAsEpochSecond, durationAsSeconds);
        }
    }

    /**
     * Checks if the given filename is one of the ignored filenames.
     * The ignored filenames contain "VictusGraphAnalyzer.zip", "Screenshot.png", and any filename containing "crash".
     *
     * @param filename The filename to check.
     * @return True if the filename is ignored, false otherwise.
     */
    private boolean fileToLoad(String filename) {
        return filename.contains("Shell")
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
}