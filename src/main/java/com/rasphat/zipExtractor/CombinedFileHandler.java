package com.rasphat.zipExtractor;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

class CombinedFileHandler extends FileHandler implements ZipHandler {

    /**
     * Handles the extraction of a ZIP file.
     *
     * @param file     the file representing the ZIP file.
     * @param path     the destination path for extracting the ZIP file contents.
     * @param password the password for the ZIP file (if encrypted).
     */
    @Override
    public void handleZip(File file, String path, char[] password) {
        if (file == null || !isZipFile(file)) {
            System.out.println("File is null, or is not a valid ZIP file");
            return;
        }

        try {
            ZipFile zipFile = new ZipFile(file);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(path);
        } catch (ZipException e) {
            handleException(e);
        }
    }
}
