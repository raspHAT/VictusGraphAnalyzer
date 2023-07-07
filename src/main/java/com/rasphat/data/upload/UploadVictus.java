package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadVictus extends Upload implements UploadProcessor {

    private final String NAME_OF_PROPERTY = "app."+UploadType.VICTUS.name();
    private final Logger logger = LoggerFactory.getLogger(UploadVictus.class);

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        logger.info(UploadVictus.class.getName());

        extractZip(multipartFile, getPasswordFromProperty(NAME_OF_PROPERTY));

        try {
            uploadDataList = processFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

        averageDuration = calculateDurationFromUploadDataList();
        System.out.println(calculateAverageDuration(averageDuration));

        //deleteTempDirectory();

        return uploadDataList;
    }

    private List<Duration> calculateDurationFromUploadDataList() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
        String regex = "\\d{1,2}\\/\\d{1,2}\\/\\d{4} \\d{1,2}:\\d{2}:\\d{2} [AP]M";
        Pattern pattern = Pattern.compile(regex);
        List<Duration> durationList = new ArrayList<>();
        for (UploadData uploadDate :uploadDataList) {
            Matcher uploadDate1 = pattern.matcher(uploadDate.getRawLine());
            if (uploadDate1.find()) {
                LocalDateTime localDateTime = LocalDateTime.parse(uploadDate1.group(), formatter);
                String dateTimeString = uploadDate1.group();
                Duration duration = Duration.between(uploadDate.getLocalDateTime(), localDateTime);
                durationList.add(duration);
            }
        }
        System.out.println(durationList.size());

        return durationList;
    }

    public static Duration calculateAverageDuration(List<Duration> durationList) {
        long totalNanos = durationList.stream()
                .mapToLong(Duration::toNanos)
                .sum();

        double averageNanos = totalNanos / (double) durationList.size();

        return Duration.ofNanos((long) averageNanos);
    }

}