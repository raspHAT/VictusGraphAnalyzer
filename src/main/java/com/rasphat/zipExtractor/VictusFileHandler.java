package com.rasphat.zipExtractor;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

/**
 * The VictusZipHandler class handles extraction of ZIP files with Victus encryption.
 */
class VictusFileHandler extends FileHandler implements ZipHandler {

    /**
     * Handles a given ZIP file with Victus encryption.
     *
     * @param file     the ZIP file to be handled.
     * @param path     the path where the ZIP file should be extracted.
     * @param password the password to be used to decrypt the ZIP file, if encrypted.
     */
    @Override
    public void handleZip(File file, String path, char[] password) {
        if (file == null || !isZipFile(file)) {
            System.out.println("File is null, or is no zipfile");
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
