package com.rasphat.data.upload;

public class UploadData {
    private final String filename;
    private final String rawLine;
    private final String project;

    private String getFilename() {
        return filename;
    }

    private String getRawLine() {
        return rawLine;
    }

    private String getProject() {
        return project;
    }

    public UploadData(String filename, String rawLine, String project) {
        this.filename = filename;
        this.rawLine = rawLine;
        this.project = project;
    }

    @Override
    public String toString() {
        return "Data{" +
                "filename='" + filename + '\'' +
                ", rawLine='" + rawLine + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}
