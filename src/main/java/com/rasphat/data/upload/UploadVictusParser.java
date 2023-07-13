package com.rasphat.data.upload;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for parsing and processing upload data.
 */
public class UploadVictusParser {

    private final Logger LOGGER = LoggerFactory.getLogger(UploadVictusParser.class);
    private final SimpleRegression simpleRegression = new SimpleRegression();

    /**
     * Gets the SimpleRegression object used for regression analysis.
     *
     * @return The SimpleRegression object.
     */
    public SimpleRegression getSimpleRegression() {
        return simpleRegression;
    }

    /**
     * Extracts the AscTime from the raw line of upload data for the "message" filenames.
     *
     * @param uploadDataList The list of UploadData objects to process.
     */
    public void ascTimeFromRawline(List<UploadData> uploadDataList) {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}.*?");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        uploadDataList.stream()
                .filter(data -> data.getFilename().contains("message"))
                .forEach(data -> {
                    Matcher matcher = pattern.matcher(data.getRawLine());
                    if (matcher.find()) {
                        String dateTimeString = matcher.group();
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                        data.setLocalDateTime(dateTime);
                    }
                });
    }

    /**
     * Extracts the GuiTime from the raw line of upload data for specific filenames.
     *
     * @param uploadDataList The list of UploadData objects to process.
     */
    public void guiTimeFromRawline(List<UploadData> uploadDataList) {
        Pattern pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS");

        uploadDataList.stream()
                .filter(data -> (
                        data.getFilename().contains("Sword")
                                || data.getFilename().contains("ToolBox")
                                || data.getFilename().contains("Shell")
                ))
                .forEach(data -> {
                    Matcher matcher = pattern.matcher(data.getRawLine());
                    if (matcher.find()) {
                        String dateTimeString = matcher.group();
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                        data.setLocalDateTime(dateTime);
                    }
                });
    }

    /**
     * Extracts the OctTime from the raw line of upload data for the "HE2SOCT" filenames.
     *
     * @param uploadDataList The list of UploadData objects to process.
     */
    public void octTimeFromRawline(List<UploadData> uploadDataList) {
        Pattern pattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss.SSS");

        uploadDataList.stream()
                .filter(data -> (
                        data.getFilename().contains("HE2SOCT")
                ))
                .forEach(data -> {
                    Matcher matcher = pattern.matcher(data.getRawLine());
                    if (matcher.find()) {
                        String dateTimeString = matcher.group();
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                        data.setLocalDateTime(dateTime);
                    }
                });
    }

    /**
     * Sets the LocalDateTime of the UploadData objects in the provided list to the current LocalDateTime if it is null.
     *
     * @param uploadDataList The list of UploadData objects to check and modify.
     */
    public void nullTimeFromRawline(List<UploadData> uploadDataList) {
        uploadDataList.forEach(data -> {
            if (data.getLocalDateTime() == null) {
                data.setLocalDateTime(LocalDateTime.now());
            }
        });
    }

    /**
     * Processes a list of UploadData objects, extracts "DMS" filenames, calculates the duration between
     * the rawLine LocalDateTime and the UploadData LocalDateTime, and stores the result in a map.
     *
     * @param uploadDataList The list of UploadData objects to process.
     * @return A map with LocalDateTime keys and Duration values. Each entry represents a LocalDateTime and the
     * duration between it and another LocalDateTime extracted from the same UploadData object.
     */
    public Map<LocalDateTime, Duration> processUploadDataList(List<UploadData> uploadDataList) {
        Map<LocalDateTime, Duration> dateTimeDurationMap = new HashMap<>();

        List<UploadData> filteredByDmsList = uploadDataList.stream()
                .filter(uploadData -> uploadData.getFilename().contains("DMS"))
                .collect(Collectors.toList());

        for (UploadData uploadData : filteredByDmsList) {
            LocalDateTime localDateTimeAsc = extractAscTimeFromGuiLog(uploadData.getRawLine());
            LocalDateTime localDateTimeGui = uploadData.getLocalDateTime();

            Duration duration = Duration.between(localDateTimeAsc, localDateTimeGui);
            dateTimeDurationMap.put(localDateTimeGui, duration);
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
    protected void populateTimeDurationMap(Map<LocalDateTime, Duration> timestampsDurationsMap) {
        if (timestampsDurationsMap == null || timestampsDurationsMap.isEmpty()) {
            throw new IllegalArgumentException("Invalid input map");
        }

        for (Map.Entry<LocalDateTime, Duration> entry : timestampsDurationsMap.entrySet()) {
            LocalDateTime timestamp = entry.getKey();
            Duration duration = entry.getValue();
            double timestampAsEpochMilli = timestamp.toInstant(ZoneOffset.UTC).toEpochMilli();
            double durationInMillis = duration.toMillis();
            simpleRegression.addData(timestampAsEpochMilli, durationInMillis);
        }
    }

    /**
     * Extracts a LocalDateTime from a rawLine string.
     *
     * @param rawLine A string that contains a LocalDateTime in a specific format.
     * @return A LocalDateTime object extracted from the rawLine string, or null if the extraction was not successful.
     */
    static LocalDateTime extractAscTimeFromGuiLog(String rawLine) {
        Pattern pattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[AP]M");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

        if (rawLine != null) {
            Matcher matcher = pattern.matcher(rawLine);
            if (matcher.find()) {
                String dateTimeString = matcher.group();
                return LocalDateTime.parse(dateTimeString, formatter);
            }
        }
        return LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    }

    /**
     * Corrects a provided LocalDateTime by a predicted offset using simple linear regression.
     *
     * @param dateTime The original LocalDateTime to correct.
     * @return A new LocalDateTime, corrected by the predicted offset.
     */
    public LocalDateTime correctDateTime(LocalDateTime dateTime) {
        long originalMilliseconds = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        long offsetMilliseconds = (long) simpleRegression.predict(originalMilliseconds);
        return dateTime.plus(Duration.ofMillis(offsetMilliseconds));
    }
}