package com.rasphat.archiveExtractor;

import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

public abstract class AbstractHandler {

    // The directory path to where the ZIP files are extracted temporarily.
    public static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "extractZip" + File.separator;

    private static final String WRONG_PASSWORD_MSG = "Wrong password";
    private static final String CORRUPT_FILE_MSG = "Most likely: Corrupt file, or not from type Victus!";

    /**
     * Deletes the extracted data and its directory.
     *
     * @throws IOException if an I/O error occurs.
     */
    public static void deleteExtractedData() throws IOException {
        File directoryToBeDeleted = new File(TEMP_DIR_PATH);
        deleteDirectory(directoryToBeDeleted);
    }

    private static void deleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        if (!directoryToBeDeleted.delete()) {
            throw new IOException("Failed to delete " + directoryToBeDeleted);
        }
    }

    /**
     * Handles a given ZipException.
     *
     * @param e the ZipException to be handled.
     */
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
     * Handles a given IOException.
     *
     * @param e the IOException to be handled.
     */
    public void handleIOException(IOException e) {
        System.out.println(e.getMessage());
    }

    /**
     * Checks if the provided file is a valid ZIP file.
     *
     * @param file the file to be checked.
     * @return true if the file is a valid ZIP file, false otherwise.
     */
    protected boolean isZipFile(File file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            return zipInputStream.getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }
}
