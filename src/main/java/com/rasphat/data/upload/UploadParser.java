package com.rasphat.data.upload;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UploadParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadParser.class);
    private final SimpleRegression simpleRegression = new SimpleRegression();

    public SimpleRegression getSimpleRegression() {
        return simpleRegression;
    }

    public void extractLocalDateTimeFromUploadDateRawlineAsc(List<UploadData> uploadDataList) {
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


    public void extractLocalDateTimeFromUploadDateRawlineGui(List<UploadData> uploadDataList) {
        Pattern pattern = Pattern.compile( "\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?");
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

    public void extractLocalDateTimeFromUploadDateRawlineOct(List<UploadData> uploadDataList) {
        Pattern pattern = Pattern.compile( "\\d{1,2}/\\d{1,2}/\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}");
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

    public void ifDateIsNullSetToUnixTime(List<UploadData> uploadDataList) {
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
     *         duration between it and another LocalDateTime extracted from the same UploadData object.
     */
    public Map<LocalDateTime, Duration> processUploadDataList(List<UploadData> uploadDataList) {

        // Map to hold LocalDateTime as key and Duration as value
        Map<LocalDateTime, Duration> dateTimeDurationMap = new HashMap<>();

        // Filter the list for filenames containing "DMS"
        List<UploadData> filteredList = uploadDataList.stream()
                .filter(uploadData -> uploadData.getFilename().contains("DMS"))
                .collect(Collectors.toList());

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
        } for (Map.Entry<LocalDateTime, Duration> entry : timestampsDurationsMap.entrySet()) {
            double timestampAsEpochSecond = entry.getKey().toEpochSecond(ZoneOffset.UTC);
            double durationAsSeconds = entry.getValue().getSeconds();

            getSimpleRegression().addData(timestampAsEpochSecond, durationAsSeconds);
        }
    }

    /**
     * This method extracts a LocalDateTime from a rawLine string.
     *
     * @param rawLine A string that contains a LocalDateTime in a specific format.
     * @return A LocalDateTime object extracted from the rawLine string, or null if the extraction was not successful.
     */
    static LocalDateTime extractLocalDateTimeFromRawLine(String rawLine) {

        // Define pattern and formatter
        Pattern PATTERN = Pattern.compile("\\d{1,2}\\/\\d{1,2}\\/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}\\s[AP]M");
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

        if (rawLine != null) {
            // Find the date time substring using the pattern
            Matcher matcher = PATTERN.matcher(rawLine);
            if (matcher.find()) {
                String dateTimeString = matcher.group();
                return LocalDateTime.parse(dateTimeString, FORMATTER);
            }
        }
        return LocalDateTime.of(1970,1, 1,0,0,0);
    }

    /**
     * This method corrects a provided LocalDateTime by a predicted offset.
     * The offset is predicted using simple linear regression.
     *
     * @param dateTime The original LocalDateTime to correct.
     * @return A new LocalDateTime, corrected by the predicted offset.
     */
    public LocalDateTime correctDateTime(LocalDateTime dateTime) {
        // Convert the original LocalDateTime to epoch milliseconds
        long originalMilliseconds = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        // Predict the offset in milliseconds using SimpleRegression
        long offsetMilliseconds = (long) simpleRegression.predict(originalMilliseconds);

        // Correct the original LocalDateTime by the predicted offset and return
        return dateTime.plus(Duration.ofMillis(offsetMilliseconds));
    }
}