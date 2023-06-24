package com.rasphat.data.utils;

public interface LogFileOperations {
    void copyFile(String sourcePath, String destinationPath);
    void moveFile(String sourcePath, String destinationPath);
    void deleteFile(String filePath);
}
