package com.rasphat.zipExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The ZipExtractor class is responsible for extracting ZIP files.
 */
public class Extractor {

    // A map that associates ZipHandler instances with their corresponding passwords.
    private final Map<String, ZipHandler> handlers = new HashMap<>();


    /**
     * Constructor for the ZipExtractor class.
     * Initializes handlers for specific passwords.
     */
    public Extractor() {
        handlers.put("pipiskamanakonja", new VictusZipHandlerAbstract());
        handlers.put("Tokio$%Server12", new CombinedZipHandlerAbstract());
    }

    /** for Mocking?!? 2023-06-03
     * This method is used to add a new handler to the handlers Map.
     * Each handler is responsible for handling a specific type of ZIP file
     * and is associated with a password that is used to decrypt the ZIP file.
     *
     * @param password A string that represents the password to decrypt the ZIP file.
     * @param handler An instance of a class that implements the ZipHandler interface.
     *                This handler will be used whenever a ZIP file encrypted with the provided password is encountered.
     */
    public void addHandler(String password, ZipHandler handler) {
        handlers.put(password, handler);
    }

    /**
     * Extracts a ZIP file given its bytes and password.
     *
     * @param bytes    the bytes of the ZIP file to be extracted.
     * @param password the password for the ZIP file.
     * @throws IOException if an I/O error occurs.
     */
    public void extractZip(byte[] bytes, String password, String contentType) throws IOException {
        // Creation of a temporary file and writing the bytes to it.
        // MultipartFile file = new MultipartFile();
        File tempZipFile = File.createTempFile( contentType + "_extractZip", ".zip");
        try (FileOutputStream fos = new FileOutputStream(tempZipFile)) {
            fos.write(bytes);
        }

        // Handling and extraction of the ZIP file.
        try {
            ZipHandler handler = handlers.get(password);
            if (handler != null) {
                handler.handleZip(tempZipFile, ZipHandlerAbstract.TEMP_DIR_PATH, password.toCharArray());
            } else {
                throw new IllegalArgumentException("No handler for password " + password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }   //finally {
            // Ensuring the deletion of the temporary ZIP file.
            //if (!tempZipFile.delete()) {
            // tempZipFile.deleteOnExit();
            //}
    }
}