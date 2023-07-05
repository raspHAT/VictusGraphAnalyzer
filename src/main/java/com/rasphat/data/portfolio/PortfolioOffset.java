package com.rasphat.data.portfolio;

import com.rasphat.data.upload.UploadData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PortfolioOffset {

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
        Duration offset = Duration.between(uploadTime, messageTime);
        return offset;
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
