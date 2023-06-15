package com.rasphat.archiveHandler;

import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.zip.ZipInputStream;

public abstract class Handler implements ZipHandler {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    // The directory path to where the ZIP files are extracted temporarily.
    public static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "extractZip" + File.separator;


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

    //String handlerString = properties.getProperty("zipHandler.victus");
    //Handler victusHandler = HandlerFactory.createHandler(handlerString);


    /**
     * Handles a given ZipException.
     *
     * @param e the ZipException to be handled.
     */
    public void handleException(ZipException e) {
        if (e.getType() == ZipException.Type.WRONG_PASSWORD) {
            String WRONG_PASSWORD_MSG = "File is from type 'ZIP' but password is wrong!";
            logger.info(WRONG_PASSWORD_MSG);
            logger.debug(e.getType().toString());
            logger.debug(e.getMessage() + "Message");
            logger.debug(e.getLocalizedMessage());
            logger.debug(e.getClass().toString());
        } else if (e.getMessage().equals("Zip headers not found. Probably not a zip file")) {
            logger.info(e.getMessage());

            e.printStackTrace();
        } else {
            String CORRUPT_FILE_MSG = "Most likely: Corrupt file, or not from type Victus!";
            logger.info(CORRUPT_FILE_MSG);
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
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            return zipInputStream.getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }


}
