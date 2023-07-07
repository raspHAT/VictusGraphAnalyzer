package com.rasphat.controller;

import com.rasphat.data.portfolio.DateParser;
import com.rasphat.data.portfolio.PortfolioOffset;
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

import javax.sound.sampled.Port;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static com.rasphat.data.portfolio.PortfolioOffset.getCalculatedDuration;

@Controller
public class MainController implements ErrorController {

    /**
     * The MainController class serves as a controller component in Spring Framework.
     * It implements the ErrorController interface, which is a part of Spring Boot.
     * The ErrorController interface provides a mechanism to handle errors and
     * customize error pages in the application.
     * By implementing the ErrorController interface, the MainController
     * takes on the responsibility of handling error navigation.
     * Whenever an error occurs, the MainController will be invoked to handle it.
     * In this specific case, when the /error path is accessed, the MainController
     * redirects to an error.html file. This file contains the customized content
     * that will be presented to the user when an error occurs.
     */

    private final Logger logger = LoggerFactory.getLogger(MainController.class);


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

                //new PortfolioOffset(uploadDataList);

                //for (UploadData inputList : uploadDataList) {
                 //   if (!inputList.getFilename().contains("Screenshot.png")
                 //   && !inputList.getFilename().contains("DS_Store"))
                  //  {
                        // Add your code here to process each list of UploadData
                        //LocalDateTime localDateTime = DateParser.findDateTimeInString(inputList.getRawLine());
                       // if ( localDateTime == null)
                         //   System.out.println("Yippi: " + inputList.getRawLine() + " " + inputList.getFilename());
                       // else System.out.println("Yippi-NON-NULLI: " + inputList.getRawLine() + " " + inputList.getFilename());
                   // }
               // }

                //List<UploadData> uploadDataOffsetList = PortfolioOffset.getFilteredUploadDataList(uploadDataList);
                //Duration test = PortfolioOffset.getCalculatedDuration(uploadDataOffsetList);




                System.out.println(uploadDataList.size());
                System.out.println(uploadDataList.get(1));
                System.out.println(uploadDataList.get(uploadDataList.size()-1));
                System.out.println(uploadDataList.get((uploadDataList.size()-1)/2));
                logger.info("Upload successfully!");
                uploadDataList.stream()
                        .filter(uploadData -> uploadData.getLocalDateTime() != null)
                        .forEach(System.out::println);

                /*
                2022-04-30T02:18:10.702388+00:00 asc28 CAL: D: === OnPLCStateChanged 36 ===
                    <td>Sat May  7 09:36:54 UTC 2022
                05/06/2022 09:16:45.062  :)  t:14 (engine Camera.Motor) base::CameraBase.Dispose_(): Dispose
                - <4/23/2022 07:50:10.934> ERROR: No swept source controller created.
                */

                return "redirect:/success";

            } catch (Exception e) {
                logger.info(e.getMessage());
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