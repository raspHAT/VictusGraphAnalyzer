package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the uploading of data specific to the 'Victus' upload type.
 * Extends the abstract class 'Upload' and implements the 'UploadProcessor' interface.
 */
public class UploadVictus extends Upload implements UploadProcessor {

    private static final String NAME_OF_PROPERTY = UploadType.VICTUS.name();
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadVictus.class);


    /**
     * Processes the upload data specific to the 'Victus' upload type.
     *
     * @param multipartFile The data to upload.
     */
    @Override
    public void processUploadData(MultipartFile multipartFile) {
        LOGGER.info("Processing upload data from: {}", getClass());

        // Extracts zip file content using the provided property password
        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        // Create the temp folder with a shutdown hook
        createTempDirectory();

        extractZip(multipartFile, password);

        // Process extracted files and store in uploadDataList
        processFiles();

        UploadParser uploadParser = new UploadParser();
        uploadParser.extractLocalDateTimeFromUploadDateRawlineAsc(uploadDataList);
        uploadParser.extractLocalDateTimeFromUploadDateRawlineGui(uploadDataList);
        uploadParser.extractLocalDateTimeFromUploadDateRawlineOct(uploadDataList);
        uploadParser.ifDateIsNullSetToUnixTime(uploadDataList);

        // Perform regression analysis on the data
        uploadParser.calculateRegression(uploadParser.processUploadDataList(uploadDataList));

        System.out.println(uploadParser.getSimpleRegression().getIntercept());
        System.out.println(uploadParser.getSimpleRegression().getRSquare());

        uploadDataList.stream()
                .filter(data -> data.getFilename().contains("message"))
                .forEach(data -> {
                    LocalDateTime originalDateTime = data.getLocalDateTime();
                    LocalDateTime correctedDateTime = uploadParser.correctDateTime(originalDateTime);
                    data.setLocalDateTime(correctedDateTime);
                });



        // Sort the data by datetime
        uploadDataList.sort(Comparator.comparing(UploadData::getLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Checks if the given filename is one of the ignored filenames.
     * The ignored filenames contain "VictusGraphAnalyzer.zip", "Screenshot.png", and any filename containing "crash".
     *
     * @param filename The filename to check.
     * @return True if the filename is ignored, false otherwise.
     */
    private boolean fileToLoad(String filename) {
        return filename.contains("Shell")
                || filename.contains("Sword")
                || filename.contains("ToolBox")
                || filename.contains("messages")
                || filename.equals("HE2SOCT.log")
                || filename.equals("Machine.xml")
                || filename.equals("Sword.xml")
                || filename.equals("SwordTesting.xml")
                || filename.equals("SystemTest.xml")
                || filename.equals("Toolbox.xml")
                || filename.equals("WebDiag.html");
    }

}