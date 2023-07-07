package com.rasphat.data.portfolio;

import com.rasphat.data.upload.UploadData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PortfolioOffset {

    private Map<String, LocalDateTime> uploadDataMap;

    public PortfolioOffset(List<UploadData> uploadDataList) {
        List<UploadData> filteredList = filterUploadDataList(uploadDataList);

        sortUploadDataListDescending(filteredList);
        createUploadDataMap(filteredList);

    }

    private List<UploadData> filterUploadDataList(List<UploadData> uploadDataList) {
        return uploadDataList.stream()
                .filter(uploadData -> uploadData.getFilename().equals("messages"))
                .collect(Collectors.toList());
    }

    private void sortUploadDataListDescending(List<UploadData> uploadDataList) {
        Comparator<UploadData> dateTimeComparator = Comparator.comparing(UploadData::getLocalDateTime).reversed();
        uploadDataList.sort(dateTimeComparator);
    }


    public void createUploadDataMap(List<UploadData> uploadDataList) {
        uploadDataMap = new HashMap<>();

        for (UploadData uploadData : uploadDataList) {
            String rawLine = uploadData.getRawLine();
            LocalDateTime localDateTime = uploadData.getLocalDateTime();

            uploadDataMap.put(rawLine, localDateTime);
        }
    }

    public Map<String, LocalDateTime> getUploadDataMap() {
        return uploadDataMap;
    }



    public static List<UploadData> getFilteredUploadDataList(List<UploadData> uploadDataList) {
        return uploadDataList.stream()
                .filter(uploadData -> uploadData.getFilename().equals("messages"))
                .collect(Collectors.toList());
    }

    public static Duration getCalculatedDuration(List<UploadData> filteredUploadDataList) {
        List<Duration> offsetList = filteredUploadDataList.stream()
                .map(PortfolioOffset::extractOffsetFromUploadData)
                .collect(Collectors.toList());

        return calculateAverageOffset(offsetList);
    }

    private static Duration extractOffsetFromUploadData(UploadData uploadData) {
        String rawLine = uploadData.getRawLine();
        String timestamp = extractTimestampFromRawLine(rawLine);
        LocalDateTime messageTime = parseTimestamp(timestamp);
        LocalDateTime uploadTime = uploadData.getLocalDateTime();
        return Duration.between(uploadTime, messageTime);
    }

    private static String extractTimestampFromRawLine(String rawLine) {
        int startIndex = rawLine.indexOf("time: ") + 6;
        int endIndex = rawLine.indexOf(",", startIndex);
        return rawLine.substring(startIndex, endIndex).trim();
    }

    private static LocalDateTime parseTimestamp(String timestamp) {
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"));
    }

    private static Duration calculateAverageOffset(List<Duration> offsetList) {
        long totalSeconds = offsetList.stream()
                .mapToLong(Duration::getSeconds)
                .sum();

        long averageSeconds = totalSeconds / offsetList.size();

        return Duration.ofSeconds(averageSeconds);
    }
}
