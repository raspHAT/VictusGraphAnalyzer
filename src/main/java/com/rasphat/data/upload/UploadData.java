package com.rasphat.data.upload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single data item to be uploaded.
 */
public class UploadData {
    private final String filename;
    private final String rawLine;
    private final String device;
    private LocalDateTime localDateTime;

    /**
     * Initializes a new instance of the UploadData class.
     *
     * @param filename The name of the file the data originated from.
     * @param rawLine The raw text of the data line.
     * @param device The name of the project associated with the data.
     * @param localDateTime The date and time associated with the data.
     */
    public UploadData(String filename, String rawLine, String device, LocalDateTime localDateTime) {
        this.filename = filename;
        this.rawLine = rawLine;
        this.device = device;
        this.localDateTime = localDateTime;
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
    public String getDevice() {
        return device;
    }

    /**
     * SGts the date and time associated with the data.
     *
     * @return The date and time to set.
     */
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    /**
     * Sets the date and time associated with the data.
     *
     * @param localDateTime The date and time to set.
     */
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
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
                ", device='" + device + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }

    /**
     * Returns a string representation of the upload data.
     *
     * @return A string representing the upload data.
     */
    public String stringToSaveInFile() {
        String formattedDateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        if ( localDateTime != null) {
            formattedDateTime= localDateTime.format(formatter);
        } else formattedDateTime = null;
        StringBuilder adjustedFilename = new StringBuilder(filename.substring(0, Math.min(filename.length(), 60)));
        // If filename is shorter than 10 characters, fill with spaces
        while (adjustedFilename.length() < 60) {
            adjustedFilename.append(" ");}


        //return formattedDateTime + " " + adjustedFilename +  " " + rawLine;
    return formattedDateTime + " " + adjustedFilename + " " + rawLine;
    }
}
