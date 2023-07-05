package com.rasphat.data.portfolio;

import com.rasphat.data.upload.UploadData;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DateParser {
    public static LocalDateTime findDateTimeInString(String input) {
        LocalDateTime dateTime = null;

        String regexPattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}.*?|" +
                "\\w{3} \\w{3}.+\\d{1,2} \\d{2}:\\d{2}:\\d{2} UTC \\d{4}.*?|" +
                "\\d{2}.\\d{2}.\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?|" +
                "\\d{1,2}.\\d{1,2}.\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String matchedDate = matcher.group();
            dateTime = parseDateTime(matchedDate);
        }

        return dateTime;
    }

    private static LocalDateTime parseDateTime(String input) {
        LocalDateTime dateTime = null;

        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS",        // ASC Zeit
                "EEE MMM d HH:mm:ss zzz yyyy",      // WEBDIAG Zeit
                "MM/dd/yyyy HH:mm:ss.SSS",          // GUI LOGS
                "M/d/yyyy HH:mm:ss.SSS"    // & OCT Logs
                // Weitere unterstützte Formate hier hinzufügen
        };

        for (String format : formats) {
            try {
                //System.out.println(input + " " + format);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                dateTime = LocalDateTime.parse(input, formatter);
                break;
            } catch (Exception e) {

                //System.out.println(e.getMessage() + format + " :" + input);
                // Das Format passt nicht, versuche das nächste
                //System.out.println(dateTime + " String: " + input);

            }
        }

        return dateTime;
    }




    // 05/01/2022 09:01:35.045  :)  MessageReceiver: << DMS changed - time: 5/1/2022 1:21:17 AM, force: 1.0000, state: Error

    private Duration createTimeOffsetList(LocalDateTime localDateTime, String rawLine) {
        String REGEX_OFFSET_TYPE_ONE = "(\\d{1,2})[/](\\d{1,2})[/](\\d{1,4})[ ](\\d{1,2}[:]\\d{1,2}[:]\\d{1,2})[ ](.M)";
        String REGEX_OFFSET_TYPE_TWO = "(\\d{1,2})[-](\\w{2,3})[-](\\d{1,4})[ ](\\d{1,2}[:]\\d{1,2}[:]\\d{1,2})[ ](.M)";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d h:mm:ss a", Locale.ENGLISH);

        Matcher matcherTypeOne = Pattern.compile(REGEX_OFFSET_TYPE_ONE).matcher(rawLine);
        Matcher matcherTypeTwo = Pattern.compile(REGEX_OFFSET_TYPE_TWO).matcher(rawLine);

            if (matcherTypeOne.find()) {
                LocalDateTime localDateTimeAsc = parseDateTime(matcherTypeOne, formatter, 3, 1, 2, 4, 5);
                return Duration.between(localDateTime, localDateTimeAsc);
            }

            if (matcherTypeTwo.find()) {
                LocalDateTime localDateTimeAsc = parseDateTime(matcherTypeTwo, formatter, 3, 2, 1, 4, 5);
                //getTimeOffsetList().add(new PortfolioOffset(portfolioData.getCreated(), localDateTimeAsc, Duration.between(portfolioData.getCreated(), localDateTimeAsc)));
                return Duration.between(localDateTime, localDateTimeAsc);
            }
            return null;
    }




    private LocalDateTime parseDateTime(Matcher matcher, DateTimeFormatter formatter, int yearGroup, int monthGroup, int dayGroup, int timeGroup, int meridianGroup) {
        String ascTime = matcher.group(yearGroup) + "/" +
                matcher.group(monthGroup) + "/" +
                matcher.group(dayGroup) + " " +
                matcher.group(timeGroup) + " " +
                matcher.group(meridianGroup);

        return LocalDateTime.parse(ascTime, formatter);
    }



}
