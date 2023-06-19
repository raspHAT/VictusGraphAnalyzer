package com.rasphat.data.factories.extractor.factory.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface LogFileExtractor {
    void extractLogFile(String  project, MultipartFile file);
}
