package com.rasphat.data.upload;

import java.time.LocalDateTime;

public class UploadData {
    private final String filename;
    private final String rawLine;
    private final String project;
    private final LocalDateTime localDateTime;

    public String getFilename() {
        return filename;
    }

    public String getRawLine() {
        return rawLine;
    }

    public String getProject() {
        return project;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public UploadData(String filename, String rawLine, String project, LocalDateTime localDateTime) {
        this.filename = filename;
        this.rawLine = rawLine;
        this.project = project;
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "UploadData{" +
                "filename='" + filename + '\'' +
                ", rawLine='" + rawLine + '\'' +
                ", project='" + project + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
