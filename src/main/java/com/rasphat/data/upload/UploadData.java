package com.rasphat.data.upload;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single data item to be uploaded.
 */
public class UploadData {
    private final String filename;
    private final String rawLine;
    private final String project;
    private LocalDateTime originalLocalDateTime;
    private Duration duration;
    private LocalDateTime correctedLocalDateTime;

    /**
     * Initializes a new instance of the UploadData class.
     *
     * @param filename The name of the file the data originated from.
     * @param rawLine The raw text of the data line.
     * @param project The name of the project associated with the data.
     * @param originalLocalDateTime The date and time associated with the data.
     */
    public UploadData(String filename, String rawLine, String project, LocalDateTime originalLocalDateTime, LocalDateTime correctedLocalDateTime, Duration duration) {
        this.filename = filename;
        this.rawLine = rawLine;
        this.project = project;
        this.originalLocalDateTime = originalLocalDateTime;
        this.correctedLocalDateTime = correctedLocalDateTime;
        this.duration = duration;
    }

    /**
     * Gets the name of the file the data originated from.
     *
     * @return The filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Gets the raw text of the data line.
     *
     * @return The raw line.
     */
    public String getRawLine() {
        return rawLine;
    }

    /**
     * Gets the name of the project associated with the data.
     *
     * @return The project name.
     */
    public String getProject() {
        return project;
    }

    /**
     * Gets the date and time associated with the data.
     *
     * @return The date and time.
     */
    public LocalDateTime getOriginalLocalDateTime() {
        return originalLocalDateTime;
    }

    /**
     * Sets the date and time associated with the data.
     *
     * @param originalLocalDateTime The date and time to set.
     */
    public void setLocalDateTime(LocalDateTime originalLocalDateTime) {
        this.originalLocalDateTime = originalLocalDateTime;
    }

    public void setOriginalLocalDateTime(LocalDateTime originalLocalDateTime) {
        this.originalLocalDateTime = originalLocalDateTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getCorrectedLocalDateTime() {
        return correctedLocalDateTime;
    }

    public void setCorrectedLocalDateTime(LocalDateTime correctedLocalDateTime) {
        this.correctedLocalDateTime = correctedLocalDateTime;
    }

    /**
     * Returns a string representation of the upload data.
     *
     * @return A string representing the upload data.
     */
    @Override
    public String toString() {
        return "UploadData{" +
                "filename='" + filename + '\'' +
                ", rawLine='" + rawLine + '\'' +
                ", project='" + project + '\'' +
                ", originalLocalDateTime=" + originalLocalDateTime +
                '}';
    }

    /**
     * Returns a string representation of the upload data.
     *
     * @return A string representing the upload data.
     */
    public String stringToSaveInFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedDateTime = originalLocalDateTime.format(formatter);
        StringBuilder adjustedFilename = new StringBuilder(filename.substring(0, Math.min(filename.length(), 10)));
        // If filename is shorter than 10 characters, fill with spaces
        while (adjustedFilename.length() < 10) {
            adjustedFilename.append(" ");
        }

        return formattedDateTime + " " +adjustedFilename + " " + getDuration() + " " + getCorrectedLocalDateTime()+  " " + rawLine;
    }
}
