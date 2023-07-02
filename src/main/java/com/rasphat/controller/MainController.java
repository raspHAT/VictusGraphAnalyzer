package com.rasphat.controller;

import com.rasphat.data.upload.UploadData;
import com.rasphat.data.upload.UploadFactory;
import com.rasphat.data.upload.UploadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class MainController implements ErrorController {
    // The class implements the ErrorController interface.
    // This allows it to handle error navigation, and will redirect to the provided 'error.html'
    // file when the /error path is accessed.

    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("project") String project,
            @RequestParam("file") MultipartFile file) {

        if (file != null && !file.isEmpty() && project != null && !project.isEmpty()) {
            try {

                UploadFactory uploadFactory= new UploadFactory();
                UploadProcessor uploadProcessor = uploadFactory.getUploadProcessor(project);
                List<UploadData> uploadDataList = uploadProcessor.processUploadData(file);

                System.out.println(uploadDataList.size());
                System.out.println(uploadDataList.get(1));
                System.out.println(uploadDataList.get(uploadDataList.size()-1));
                System.out.println(uploadDataList.get((uploadDataList.size()-1)/2));
                logger.info("Upload successfully!");

                // Vor dem Zurückgeben der success.html-Seite
                request.setAttribute("uploadDataList", uploadDataList);

                return "redirect:/success";

            } catch (Exception e) {
                logger.info(e.getMessage());
                return "redirect:/error";
            }
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/success-page")
    public String success(Model model) {
        model.addAttribute("uploadDataList", request.getAttribute("uploadDataList"));
        return "success.html";
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