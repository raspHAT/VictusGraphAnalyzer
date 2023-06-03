package com.rasphat.zipExtractor;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;


import java.io.File;
import java.io.IOException;

class CombinedZipHandler extends TempFolderHandler implements ZipHandler {
    private static final String WRONG_PASSWORD_MSG = "Wrong password";
    private static final String CORRUPT_FILE_MSG = "Most likely: Corrupt file, or not from type Teneo or ZDW3!";

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

    /**
     * Handles the exception that occurred during ZIP file handling.
     *
     * @param e the ZipException that occurred.
     */
    @Override
    public void handleException(ZipException e) {
        if (e.getType() == ZipException.Type.WRONG_PASSWORD) {
            System.out.println(WRONG_PASSWORD_MSG);
            System.out.println(e.getType());
            System.out.println(e.getMessage() + "Message");
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getClass());
        } else if (e.getMessage().equals("Zip headers not found. Probably not a zip file")) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } else {
            System.out.println(CORRUPT_FILE_MSG);
            e.printStackTrace();
        }
    }

    /**
     * Checks if the provided file is a valid ZIP file.
     *
     * @param file the file to be checked.
     * @return true if the file is a valid ZIP file, false otherwise.
     */
    private boolean isZipFile(File file) {
        ZipFile zipFile = new ZipFile(file);
        return zipFile.isValidZipFile();
    }


    /**
     * Handles an IOException that occurred during ZIP file handling.
     *
     * @param e the IOException that occurred.
     */
    public void handleIOException(IOException e) {
        e.printStackTrace();
    }
}
