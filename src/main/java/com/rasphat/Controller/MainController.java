package com.rasphat.Controller;

import com.rasphat.archiveHandler.ExtractionHandler;
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
    public String upload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Hier wird die Dateiverarbeitung durchgeführt
                byte[] bytes = file.getBytes();
                logger.info(file.getContentType());
                String contentType = file.getContentType();

                ExtractionHandler extractionHandler = new ExtractionHandler();
                // VICTUS
                extractionHandler.extractZip(bytes,"pipiskamanakonja", contentType);
                // TENEO and ZDW3
                // extractionHandler.extractZip(bytes,"Tokio$%Server12", contentType);

                // Führe hier die gewünschte Verarbeitung mit der Datei im Arbeitsspeicher aus
                // ...
                return "redirect:/success";
            } catch (Exception e) {
                // Fehlerbehandlung, falls ein Fehler beim Hochladen oder der Verarbeitung auftritt
                return "redirect:/error";
            }
        } else {
            // Fehlerbehandlung, wenn die hochgeladene Datei leer ist
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


