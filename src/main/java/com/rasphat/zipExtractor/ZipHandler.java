package com.rasphat.zipExtractor;

import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;

/**
 * The ZipHandler interface defines the operations that must be implemented
 * by classes that handle specific types of ZIP files.
 */
public interface ZipHandler {

    /**
     * Handles a given ZIP file.
     *
     * @param file     the ZIP file to be handled.
     * @param path     the path where the ZIP file should be extracted.
     * @param password the password to be used to decrypt the ZIP file, if encrypted.
     * @throws IOException if an I/O error occurs.
     */
    void handleZip(File file, String path, char[] password) throws IOException;


}
