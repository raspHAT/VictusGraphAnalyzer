package com.rasphat.data.upload;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CSVWriter {
    public static void writeDateTimeDurationMapToCSV(Map<LocalDateTime, Duration> dateTimeDurationMap) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(UploadConstants.TEMP_DIR_PATH+"/Test.csv"))) {
            // Schreiben Sie die Header-Zeile in die CSV-Datei
            writer.println("DateTime,Duration");

            // Schreiben Sie jede Map-Eintrag in die CSV-Datei
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            for (Map.Entry<LocalDateTime, Duration> entry : dateTimeDurationMap.entrySet()) {
                LocalDateTime dateTime = entry.getKey();
                Duration duration = entry.getValue();

                // Formatieren Sie LocalDateTime und Duration als Strings
                String formattedDateTime = dateTime.format(formatter);
                String formattedDuration = formatDuration(duration);

                // Schreiben Sie den Eintrag in die CSV-Datei
                writer.println(formattedDateTime + "," + formattedDuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60; // Extrahieren der Minuten
        long seconds = duration.getSeconds() % 60; // Extrahieren der Sekunden
        long millis = duration.toMillis() % 1000; // Extrahieren der Millisekunden

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
