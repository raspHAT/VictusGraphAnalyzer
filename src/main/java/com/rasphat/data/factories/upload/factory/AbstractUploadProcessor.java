package com.rasphat.data.factories.upload.factory;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public abstract class AbstractUploadProcessor implements UploadProcessor {
    protected void setFileContent(Upload upload, MultipartFile file) throws IOException {
        // Extract file content and set it in the upload object
        String fileContent = new String(file.getBytes());
        upload.setFileContent(fileContent);
    }
}