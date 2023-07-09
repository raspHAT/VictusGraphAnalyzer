package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadParser.class);

    private static final String DATE_TIME_FORMAT = "M/d/yyyy h:mm:ss a";
    private static final String DATE_TIME_REGEX = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}:\\d{2} [AP]M";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final Pattern PATTERN = Pattern.compile(DATE_TIME_REGEX);

    static final Pattern DATE_PATTERN = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}.*?|" +
                    "\\w{3} \\w{3}.+\\d{1,2} \\d{2}:\\d{2}:\\d{2} UTC \\d{4}.*?|" +
                    "\\d{2}.\\d{2}.\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?|" +
                    "\\d{1,2}.\\d{1,2}.\\d{4} \\d{2}:\\d{2}:\\d{2}.\\d{3}.*?"
    );

    /**
     * Extracts the first date time string that matches a predefined set of formats from the given input string.
     * This method applies a regular expression pattern to the input string to find any sequences of characters
     * that match any of the predefined date time formats. If it finds a matching sequence, it parses the sequence
     * into a LocalDateTime object and returns it.
     * If no matching sequence is found, or if a found sequence cannot be parsed into a LocalDateTime object,
     * this method returns the current date and time.
     *
     * @param file the file from which the name is checked for containing either 'Sword', 'Shell' or 'messages'.
     * If the filename contains any of these, then date time parsing is attempted.
     * @param input the string to search for a date time sequence
     * @return the first LocalDateTime object found in the input string, or the current date and time
     * if no valid date time sequence is found
     */
    public static LocalDateTime findDateTimeInString(File file, String input ) {
        //System.out.println(input);
        if (file.getName().contains("Sword")
                || file.getName().contains("Shell")
                || file.getName().contains("messages")
                || file.getName().contains("HE2SOCT")) {
            Matcher matcher = DATE_PATTERN.matcher(input);
            if (matcher.find()) {
                String matchedDate = matcher.group();
                return parseDateTime(file, matchedDate);
            }
        }
        return LocalDateTime.now();
    }

    /**
     * Attempts to parse the given string into a LocalDateTime object by applying various date formats.
     * This method attempts to transform the input string into a LocalDateTime object by iterating through various
     * predefined date and time formats. It returns the first successfully created LocalDateTime object.
     * If none of the predefined formats can successfully transform the input string into a LocalDateTime object,
     * this method throws a DateTimeParseException.
     *
     * @param input the string to be parsed
     * @return the LocalDateTime object that was created from the input string
     * @throws DateTimeParseException if the input string cannot be transformed into a LocalDateTime object
     */
    protected static LocalDateTime parseDateTime(File file, String input) {

/*        List<String> FORMATS = Arrays.asList(
                "yyyy-MM-dd'T'HH:mm:ss.SSS",        // ASC Zeit
                //"EEE MMM d HH:mm:ss zzz yyyy",      // WEBDIAG Zeit
                "M/d/yyyy HH:mm:ss.SSS",          // GUI LOGS
                "MM/dd/yyyy HH:mm:ss.SSS"    // & OCT Logs
        );*/

        String FORMAT_ASC =         "yyyy-MM-dd'T'HH:mm:ss.SSS";               // ASC time
        String FORMAT_ISO =         String.valueOf(DateTimeFormatter.ISO_DATE_TIME);
        String FORMAT_WEBDIAG =     "EEE MMM d HH:mm:ss zzz yyyy";           // WEBDIAG time
        String FORMAT_GUI =         "M/d/yyyy HH:mm:ss.SSS";                 // GUI time
        String FORMAT_OCT =         "M/d/yyyy HH:mm:ss.SSS";                 // OCT time

        if (file.getName().contains("messages")) {
            try {
                //DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_ASC);

                //System.out.println(";;;;;''''fekfjrjgjriggroi/>>>>>>>>>>"+LocalDateTime.parse(input, formatter));
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                LOGGER.error("parseDateTime, {} Error {}", DateTimeFormatter.ISO_DATE_TIME, e.getMessage());
                return LocalDateTime.of(1970,1, 1,0,0,0);
            }
        }
        else if (file.getName().contains("WebDiag")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_WEBDIAG);
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                LOGGER.error("parseDateTime, {} Error {}", FORMAT_WEBDIAG, e.getMessage());
                return LocalDateTime.of(1970,1, 1,0,0,0);
            }
        }
        else if (file.getName().contains("Sword")
                || file.getName().contains("Shell")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_GUI);
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                LOGGER.error("parseDateTime, {} Error {}", FORMAT_GUI, e.getMessage());
                return LocalDateTime.of(1970,1, 1,0,0,0);
            }
        }
        else if (file.getName().contains("HE2SOCT")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_OCT);
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                LOGGER.error("parseDateTime, {} Error {}", FORMAT_OCT, e.getMessage());
                return LocalDateTime.of(1970,1, 1,0,0,0);
            }
        } else {
            return LocalDateTime.of(1970,1, 1,0,0,0);
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
        Pattern PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if (rawLine != null) {
            // Find the date time substring using the pattern
            Matcher matcher = PATTERN.matcher(rawLine);
            if (matcher.find()) {
                String dateTimeString = matcher.group();
                return LocalDateTime.parse(dateTimeString, FORMATTER);
            } //else System.out.println(rawLine);
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
    public static LocalDateTime correctDateTime(LocalDateTime dateTime) {
        // Convert the original LocalDateTime to epoch milliseconds
        long originalMilliseconds = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        // Predict the offset in milliseconds using SimpleRegression
        long offsetMilliseconds = (long) Upload.simpleRegression.predict(originalMilliseconds);

        // Correct the original LocalDateTime by the predicted offset and return
        return dateTime.minus(Duration.ofMillis(offsetMilliseconds));
    }
}
