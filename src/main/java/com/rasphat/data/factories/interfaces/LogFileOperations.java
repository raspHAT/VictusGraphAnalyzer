package com.rasphat.data.factories.interfaces;

public interface LogFileOperations {
    void copyFile(String sourcePath, String destinationPath);
    void moveFile(String sourcePath, String destinationPath);
    void deleteFile(String filePath);
}
