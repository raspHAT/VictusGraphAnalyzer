package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles the uploading of data specific to the 'Victus' upload type.
 */
public class UploadVictus extends Upload implements UploadProcessor {

    private static final String NAME_OF_PROPERTY = "app."+UploadType.VICTUS.name();
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadVictus.class);
    private static final String DATE_TIME_FORMAT = "M/d/yyyy h:mm:ss a";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final String DATE_TIME_REGEX = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}:\\d{2} [AP]M";
    private static final Pattern PATTERN = Pattern.compile(DATE_TIME_REGEX);

    /**
     * Process the upload data.
     * @param multipartFile The data to upload.
     * @return A list of UploadData objects representing the processed data.
     */
    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        LOGGER.info("Processing upload data for {}", UploadVictus.class.getName());

        extractZip(multipartFile, getPasswordFromProperty(NAME_OF_PROPERTY));

        try {
            uploadDataList = processFiles();
        } catch (IOException e) {
            LOGGER.error("An error occurred while processing files.", e);
        }

        List<Duration> durationList = calculateDurationFromUploadDataList();
        Duration averageDuration = calculateAverageDuration(durationList);



// Sort the list based on the absolute deviation from the average.
        durationList.sort(Comparator.comparingLong(duration -> Math.abs(duration.toMillis() - averageDuration.toMillis())));

// Print the first 10 durations.
        LOGGER.info("First 10 durations by deviation from the average:");
        for (int i = 0; i < Math.min(10, durationList.size()); i++) {
            LOGGER.info(durationList.get(i).toString());
        }

// Print the last 10 durations.
        LOGGER.info("Last 10 durations by deviation from the average:");
        for (int i = Math.max(0, durationList.size() - 100); i < durationList.size(); i++) {
            LOGGER.info(durationList.get(i).toString());
        }


        LOGGER.info("Average Duration: {}", averageDuration);

        correctUploadDataLocalTimeDate(uploadDataList, averageDuration);

        uploadDataList.sort(Comparator.comparing(UploadData::getOriginalLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder())));


        return uploadDataList;
    }

    /**
     * Correct the local date time of the upload data.
     * @param uploadDataList The list of UploadData objects.
     */
    private void correctUploadDataLocalTimeDate(List<UploadData> uploadDataList, Duration averageDuration) {
        for (UploadData uploadData : uploadDataList) {
            if (uploadData.getFilename().contains("message")) {
                uploadData.setLocalDateTime(uploadData.getOriginalLocalDateTime().minus(averageDuration));
            }
        }
    }

    /**
     * Calculate the duration from the upload data list.
     * @return A list of Duration objects.
     */
    private List<Duration> calculateDurationFromUploadDataList() {

        List<Duration> durationList = new ArrayList<>();

        for (UploadData uploadData : uploadDataList) {
            try {
                String dateTimeString = parseDateTimeString(uploadData.getRawLine());
                if (dateTimeString != null) {
                    LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
                    Duration duration = Duration.between(uploadData.getOriginalLocalDateTime(), localDateTime);
                    durationList.add(duration);
                }
            }
            catch (Exception e){
                LOGGER.error("An error occurred while calculating duration. Error: {} File: {} RawLine: {} Project: {}",
                        e.getMessage(), uploadData.getFilename(), uploadData.getRawLine(), uploadData.getProject());
            }
        }

        LOGGER.info("Number of Durations Calculated: {}", durationList.size());

        return durationList;
    }

    /**
     * Parse the date-time string from the raw line.
     * @param rawLine The raw line to parse.
     * @return The parsed date-time string.
     */
    private String parseDateTimeString(String rawLine) {
        Matcher matcher = PATTERN.matcher(rawLine);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * Calculate the average duration.
     * @param durationList The list of Duration objects.
     * @return The average duration.
     */
    private static Duration calculateAverageDuration(List<Duration> durationList) {




        long totalNanos = durationList.stream()
                .mapToLong(Duration::toNanos)
                .sum();

        // Min duration
        durationList.stream()
                .min(Comparator.comparingLong(Duration::toNanos))
                .ifPresent(minDuration -> LOGGER.info("Min duration: " + minDuration));

        // Max duration
        durationList.stream()
                .max(Comparator.comparingLong(Duration::toNanos))
                .ifPresent(maxDuration -> LOGGER.info("Max duration: " + maxDuration));


        return Duration.ofNanos(Math.round(totalNanos / (double) durationList.size()));
    }





}
