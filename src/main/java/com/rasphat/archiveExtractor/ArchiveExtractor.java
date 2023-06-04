package com.rasphat.archiveExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The ArchiveExtractor class is responsible for extracting ZIP files.
 */
public class ArchiveExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveExtractor.class);

    // A map that associates ArchiveHandler instances with their corresponding passwords.
    private final Map<String, ArchiveHandler> handlers = new HashMap<>();


    /**
     * Constructor for the ArchiveExtractor class.
     * Initializes handlers for specific passwords.
     */
    public ArchiveExtractor() {
        handlers.put("pipiskamanakonja", new VictusAbstractHandler());
        handlers.put("Tokio$%Server12", new CombinedHandler());
    }

    /**
     * This method is used to add a new handler to the handlers Map.
     * Each handler is responsible for handling a specific type of ZIP file
     * and is associated with a password that is used to decrypt the ZIP file.
     *
     * @param password A string that represents the password to decrypt the ZIP file.
     * @param handler An instance of a class that implements the ArchiveHandler interface.
     *                This handler will be used whenever a ZIP file encrypted with the provided password is encountered.
     */
    public void addHandler(String password, ArchiveHandler handler) {
        handlers.put(password, handler);
    }

    /**
     * Extracts a ZIP file given its bytes and password.
     *
     * @param bytes    the bytes of the ZIP file to be extracted.
     * @param password the password for the ZIP file.
     * @param contentType the content type of the ZIP file.
     * @throws IOException if an I/O error occurs.
     */
    public void extractZip(byte[] bytes, String password, String contentType) throws IOException {
        // Creation of a temporary file and writing the bytes to it.
        File tempZipFile = File.createTempFile(contentType + "_extractZip", ".zip");
        try (FileOutputStream fos = new FileOutputStream(tempZipFile)) {
            fos.write(bytes);
        }

        // Handling and extraction of the ZIP file.
        try {
            ArchiveHandler handler = handlers.get(password);
            if (handler != null) {
                handler.handleZip(tempZipFile, AbstractHandler.TEMP_DIR_PATH, password.toCharArray());
            } else {
                throw new IllegalArgumentException("No handler for password " + password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
