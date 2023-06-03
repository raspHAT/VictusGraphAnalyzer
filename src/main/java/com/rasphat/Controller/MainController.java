package com.rasphat.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.rasphat.zipExtractor.Extractor;

@Controller
public class MainController {
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Hier wird die Dateiverarbeitung durchgeführt
                byte[] bytes = file.getBytes();
                System.out.println(file.getContentType());
                String contentType = file.getContentType();

                Extractor extractor = new Extractor();
                // VICTUS
                //extractor.extractZip(bytes,"pipiskamanakonja", contentType);
                // TENEO and ZDW3
                extractor.extractZip(bytes,"Tokio$%Server12", contentType);

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
        return "upload-success.html";
    }

    @GetMapping("/error")
    public String uploadError() {
        return "upload-error.html";
    }
}


