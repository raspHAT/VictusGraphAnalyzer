package com.rasphat.zipExtractor;

import java.io.File;
import java.io.IOException;

public abstract class TempFolderHandler {

    // The directory path to where the ZIP files are extracted temporarily.
    public static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "extractZip" + File.separator;

    /**
     * Deletes the extracted data and its directory.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void deleteExtractedData() throws IOException {
        File directoryToBeDeleted = new File(TEMP_DIR_PATH);
        deleteDirectory(directoryToBeDeleted);
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param directoryToBeDeleted the directory to be deleted.
     * @throws IOException if an I/O error occurs.
     */
    private static void deleteDirectory(File directoryToBeDeleted) throws IOException {
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
}
