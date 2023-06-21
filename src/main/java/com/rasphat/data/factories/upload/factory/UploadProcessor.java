package com.rasphat.data.factories.upload.factory;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadProcessor {
    void processFile(String  project, MultipartFile file) throws IOException;
}
