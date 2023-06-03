package com.rasphat.zipExtractor;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

class CombinedZipHandler extends TempFolderHandler implements ZipHandler {
    private static final String WRONG_PASSWORD_MSG = "Wrong password";
    private static final String CORRUPT_FILE_MSG = "Most likely: Corrupt file, or not from type Teneo or ZDW3!";

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
            zipFile.extractAll(path); //Hier lag ;) der Grund für die Exception, wenn keine zip datei!
        } catch (ZipException e) {
            handleException(e);
        }
    }

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

        }

        else {
            System.out.println(CORRUPT_FILE_MSG);
            e.printStackTrace();
        }
    }



   /* @Override
    public void handleException(ZipException e) {
        if (e.getType() == ZipException.Type.WRONG_PASSWORD) {
            System.out.println(WRONG_PASSWORD_MSG);
        } else {
            System.out.println(CORRUPT_FILE_MSG);
            // e.printStackTrace();
        }
    }*/

    public void handleIOException(IOException e) {
        e.printStackTrace();
    }

    private boolean isZipFile(File file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            return zipInputStream.getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }

}
