package com.rasphat.data.factories.operations.factory.interfaces;

public interface LogFileOperations {
    void copyFile(String sourcePath, String destinationPath);
    void moveFile(String sourcePath, String destinationPath);
    void deleteFile(String filePath);
}
