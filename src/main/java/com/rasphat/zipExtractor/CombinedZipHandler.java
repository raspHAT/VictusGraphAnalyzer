package com.rasphat.zipExtractor;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

public class CombinedZipHandler implements ZipHandler {
    private static final String WRONG_PASSWORD_MSG = "Wrong password";
    private static final String CORRUPT_FILE_MSG = "Most likely: Corrupt file, or not from type Teneo or ZDW3!";

    @Override
    public void handleZip(File file, String path, char[] password) {
        try {
            if (file != null) {
                ZipFile zipFile = new ZipFile(file);
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password);
                }
                zipFile.extractAll(path);
            }
        } catch (ZipException e) {
            handleException(e);
        }
    }

    @Override
    public void handleException(ZipException e) {
        if (e.getType() == ZipException.Type.WRONG_PASSWORD) {
            System.out.println(WRONG_PASSWORD_MSG);
        } else {
            System.out.println(CORRUPT_FILE_MSG);
            e.printStackTrace();
        }
    }
}
