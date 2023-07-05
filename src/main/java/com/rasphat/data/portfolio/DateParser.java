package com.rasphat.data.portfolio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                "MM/dd/yyyy HH:mm:ss.SSS"          // GUI LOGS
                //"MM/dd/yyyy HH:mm:ss.SSS"             // OCT Logs
                // Weitere unterstützte Formate hier hinzufügen
        };

        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                dateTime = LocalDateTime.parse(input, formatter);
                break;
            } catch (Exception e) {
                // Das Format passt nicht, versuche das nächste
                System.out.println(dateTime + input);
            }
        }

        return dateTime;
    }

    public static void main(String[] args) {
        String input = "Text mit Datum: 2022-05-06T09:16:45.062";
        LocalDateTime dateTime = findDateTimeInString(input);

        if (dateTime != null) {
            System.out.println("Gefundenes Datum: " + dateTime);
        } else {
            System.out.println("Kein gültiges Datum gefunden.");
        }
    }
}
