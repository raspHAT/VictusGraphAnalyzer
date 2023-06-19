package com.rasphat.data.factories.extractor.factory.concrete;

import com.rasphat.data.factories.extractor.factory.abstracts.Handler;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CombinedHandler extends Handler {
    private static final Logger logger = LoggerFactory.getLogger(CombinedHandler.class);

    /**
     * Handles the extraction of a ZIP file.
     *
     * @param file     the file representing the ZIP file.
     * @param path     the destination path for extracting the ZIP file contents.
     * @param password the password for the ZIP file (if encrypted).
     */
    @Override
    public void archiveHandler(File file, String path, char[] password) {
        if (file == null || isZipFile(file)) {
            logger.debug("File is null, or is not a valid ZIP file");
            return;
        }

        try {
            ZipFile zipFile = new ZipFile(file);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            logger.info("Extraction starts!");
            zipFile.extractAll(path);
            logger.info("Extraction finishes!");
        } catch (ZipException e) {
            System.out.println("I have the longest ...");
            handleException(e);
        }
    }
}
