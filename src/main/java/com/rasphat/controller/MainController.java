package com.rasphat.controller;

import com.rasphat.data.legacy.ExtractionHandler;
import com.rasphat.data.portfolio.Portfolio;
import com.rasphat.data.upload.Upload;
import com.rasphat.data.upload.UploadData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {
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


                UploadData uploadData = new Upload().processUploadData(project, file);
                Portfolio portfolio = new Portfolio(uploadData);
                System.out.println(portfolio);

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
    public String uploadSuccess() {
        return "success.html";
    }

    @GetMapping("/error")
    public String uploadError() {
        return "success.html";
    }
}


