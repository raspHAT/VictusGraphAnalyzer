package com.rasphat.data.upload;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class DurationStatistics {
    public static void calculateDurationStatistics(Map<LocalDateTime, Duration> dateTimeDurationMap) {
        // Berechnung des Durchschnitts
        double meanInMillis = dateTimeDurationMap.values()
                .stream()
                .mapToLong(Duration::toMillis)
                .average()
                .orElse(0);

        // Durchschnitt in Duration umwandeln
        Duration meanDuration = Duration.ofMillis((long) meanInMillis);

        // Berechnung des Minimums
        Duration min = dateTimeDurationMap.values()
                .stream()
                .min(Duration::compareTo)
                .orElse(Duration.ZERO);

        // Berechnung des Maximums
        Duration max = dateTimeDurationMap.values()
                .stream()
                .max(Duration::compareTo)
                .orElse(Duration.ZERO);

        // Ausgabe der Ergebnisse
        System.out.println("Mean (Milliseconds): " + meanInMillis);
        System.out.println("Mean (Duration): " + meanDuration);
        System.out.println("Min: " + min);
        System.out.println("Max: " + max);
    }
}
