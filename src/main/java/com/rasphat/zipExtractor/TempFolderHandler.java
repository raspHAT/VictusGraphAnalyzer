package com.rasphat.zipExtractor;

import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

public abstract class TempFolderHandler {

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

    /**
     * Deletes a directory and all its contents.
     *
     * @param directoryToBeDeleted the directory to be deleted.
     * @throws IOException if an I/O error occurs.
     */
    public static void deleteDirectory(File directoryToBeDeleted) throws IOException {
        // Listing all contents of the directory.
        File[] allContents = directoryToBeDeleted.listFiles();
        // Deletion of each file or subdirectory if the directory is not empty.
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        // Deletion of the directory itself.
        if (!directoryToBeDeleted.delete()) {
            throw new IOException("Failed to delete " + directoryToBeDeleted);
        }
    }


    /**
     * Handles a given ZipException.
     * This method should implement appropriate actions to be taken when a ZipException occurs.
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

        }

        else {
            System.out.println(CORRUPT_FILE_MSG);
            e.printStackTrace();
        }
    }


//    /**
//     * Handles a given ZIP file.
//     *
//     * @param file     the ZIP file to be handled.
//     * @param path     the path where the ZIP file should be extracted.
//     * @param password the password to be used to decrypt the ZIP file, if encrypted.
//     * @throws IOException if an I/O error occurs.
//     */
//    void handleZip(File file, String path, char[] password) {
//
//    }


    /**
     * Handles a given IOException.
     * This method should implement appropriate actions to be taken when a IOException occurs.
     *
     * @param e the IOException to be handled.
     */
    public void handleIOException(IOException e) {
        System.out.println(e.getMessage());
    }



    private boolean isZipFile(File file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            return zipInputStream.getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }
}
