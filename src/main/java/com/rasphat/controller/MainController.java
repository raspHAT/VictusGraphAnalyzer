package com.rasphat.controller;

import com.rasphat.data.portfolio.PortfolioVictus;
import com.rasphat.data.upload.*;
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

/**
 * MainController class handles the file uploading process and delegates the data processing to UploadProcessor.
 * It also takes care of differentiating the operating system and writing output to the appropriate location.
 */
@Controller
public class MainController extends Upload implements ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

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
            setProject(project);
            UploadFactory uploadFactory= new UploadFactory();
            UploadProcessor uploadProcessor = uploadFactory.getUploadProcessor(project);
            uploadProcessor.processUploadData(file);

            writeToFile();

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

    @GetMapping("/portfolio")
    public String portfolio() {
        PortfolioVictus portfolioVictus = new PortfolioVictus();
        portfolioVictus.createGuidMap();
        return "portfolio.html";
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

    /**
     * Helper method to write data to a specific file.
     */
    public void writeToFile() {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(new File(UploadConstants.TEMP_DIR_COMBINED_LOGS).toPath()))) {
            getUploadDataList().stream()
                    .map(UploadData::toString)
                    .forEach(writer::println);
        } catch (IOException e) {
            LOGGER.error("Error writing to file: ", e);
        }
    }
}