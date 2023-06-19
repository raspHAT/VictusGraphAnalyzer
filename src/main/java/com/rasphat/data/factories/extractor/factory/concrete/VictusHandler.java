package com.rasphat.data.factories.extractor.factory.concrete;

import com.rasphat.data.factories.extractor.factory.abstracts.Handler;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The VictusZipHandler class handles extraction of ZIP files with Victus encryption.
 */
public class VictusHandler extends Handler {

    private static final Logger logger = LoggerFactory.getLogger(VictusHandler.class);

    public VictusHandler() {
        System.out.println("Constructor VictusHandler");
    }

    /**
     * Handles a given ZIP file with Victus encryption.
     *
     * @param file     the ZIP file to be handled.
     * @param path     the path where the ZIP file should be extracted.
     * @param password the password to be used to decrypt the ZIP file, if encrypted.
     */
    @Override
    public void archiveHandler(File file, String path, char[] password) {
        if (file == null || isZipFile(file)) {
            logger.info("File is null, or is no zipfile");
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
}
