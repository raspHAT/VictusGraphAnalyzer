package com.rasphat.uploadConverter;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class InMemoryZipConverter {
    public void converterZipInMemory(byte[] bytes, String password) throws IOException {

        File tempZipFile = File.createTempFile("temp", ".zip");
        try (FileOutputStream fos = new FileOutputStream(tempZipFile)) {
            fos.write(bytes);
        }

        try {
            ZipFile zipFile = new ZipFile(tempZipFile, password.toCharArray());
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            for (FileHeader fileHeader : fileHeaders) {
                if (!fileHeader.isDirectory()) {
                    File outputFile = File.createTempFile(fileHeader.getFileName(), null);
                    outputFile.deleteOnExit(); // Optional, entfernt die Datei, wenn die JVM beendet wird.
                    zipFile.extractFile(fileHeader.getFileName(), outputFile.getParent(), outputFile.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!tempZipFile.delete()) {
                tempZipFile.deleteOnExit();
            }
        }
    }
}