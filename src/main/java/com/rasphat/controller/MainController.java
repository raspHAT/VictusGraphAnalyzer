package com.rasphat.controller;

import com.rasphat.data.upload.UploadData;
import com.rasphat.data.upload.UploadFactory;
import com.rasphat.data.upload.UploadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

/**
 * MainController class handles the file uploading process and delegates the data processing to UploadProcessor.
 * It also takes care of differentiating the operating system and writing output to the appropriate location.
 */
@Controller
public class MainController implements ErrorController {

    private static final String TEMP_LOGS_COMBINED = "TEMP_LOGS_COMBINED.txt";
    private static final File MAC_OS_DESKTOP_VICTUS_TXT_FILE = new File(System.getProperty("user.home") + "/Desktop/" + TEMP_LOGS_COMBINED);
    private static final File WINDOWS_DESKTOP_VICTUS_TXT_FILE = new File(System.getenv("USERPROFILE") + "\\OneDrive - Bausch & Lomb, Inc\\Desktop\\" + TEMP_LOGS_COMBINED);
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Serves the index.html page.
     * @return a string representing the path to index.html.
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    /**
     * Handles the upload of files.
     * Processes the data and writes the output to a specific location based on the operating system.
     * @param project represents the project associated with the upload.
     * @param file represents the file being uploaded.
     * @return a string representing the path to the next page, depending on whether the upload was successful or not.
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("project") String project,
                         @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty() || project == null || project.isEmpty()) {
            return "redirect:/error";
        }

        try {
            UploadFactory uploadFactory= new UploadFactory();
            UploadProcessor uploadProcessor = uploadFactory.getUploadProcessor(project);
            List<UploadData> uploadDataList = uploadProcessor.processUploadData(file);

            if (isOSWindows()) {
                LOGGER.info("This is Windows");
                writeToFile(uploadDataList, WINDOWS_DESKTOP_VICTUS_TXT_FILE);
            } else if (isOSMac()) {
                LOGGER.info("This is Mac");
                writeToFile(uploadDataList, MAC_OS_DESKTOP_VICTUS_TXT_FILE);
            } else {
                LOGGER.info("Your OS is not supported");
                return "redirect:/error";
            }

            LOGGER.info("Upload successfully!");

            return "redirect:/success";

        } catch (Exception e) {
            LOGGER.error("Exception in {}, method: {}(), line: {}",
                    getClass().toString(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()
            );
            LOGGER.error("getMessage(): " + e.getMessage());
            return "redirect:/error";
        }
    }

    /**
     * Helper method to write data to a specific file.
     * @param uploadDataList represents the list of UploadData to write.
     * @param file represents the file to write to.
     */
    private void writeToFile(List<UploadData> uploadDataList, File file) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file.toPath()))) {
            uploadDataList.stream()
                    .filter(uploadData -> uploadData.getLocalDateTime() != null)
                    .map(UploadData::stringToSaveInFile)
                    .forEach(writer::println);
        } catch (IOException e) {
            LOGGER.error("Error writing to file: ", e);
        }
    }

    /**
     * Helper method to check if the operating system is Windows.
     * @return true if the operating system is Windows, false otherwise.
     */
    private boolean isOSWindows() {
        return OS.contains("win");
    }

    /**
     * Helper method to check if the operating system is Mac.
     * @return true if the operating system is Mac, false otherwise.
     */
    private boolean isOSMac() {
        return OS.contains("mac");
    }

    /**
     * Serves the success.html page.
     * @return a string representing the path to success.html.
     */
    @GetMapping("/success")
    public String success() {
        return "success.html";
    }

    /**
     * Serves the error.html page.
     * @return a string representing the path to error.html.
     */
    @GetMapping("/error")
    public String error() {
        return "error.html";
    }
}