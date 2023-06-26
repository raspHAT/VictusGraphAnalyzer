package com.rasphat.controller;

import com.rasphat.data.portfolio.Portfolio;
import com.rasphat.data.upload.Upload;
import com.rasphat.data.upload.UploadData;
import com.rasphat.data.upload.UploadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class MainController implements ErrorController {  // use interface ErrorController, so the given error.html file will be redirected from /error page !
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("project") String project,
            @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {

                UploadProcessor uploadProcessor = Upload.getUploadProcessor(project);
                List<UploadData> uploadDataList = uploadProcessor.processUploadData(file);
                return "redirect:/success";

            } catch (Exception e) {
                logger.info(String.valueOf(e));
                return "redirect:/error";
            }
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/success")
    public String success() {
        return "success.html";
    }

    @GetMapping("/error")
    public String error() {
        return "error.html";
    }


}