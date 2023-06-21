package com.rasphat.data.factories.upload.factory;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UploadFactory {
    public static Upload createUpload(String project, MultipartFile file) throws IOException {
        UploadProcessor processor = getUploadProcessor(project);
        Upload upload = new Upload();
        processor.processFile(project, file);
        return upload;
    }

    private static UploadProcessor getUploadProcessor(String project) {
        if (project.equals("Victus")) {
            return new Victus();
        } else if (project.equals("Teneo")) {
            return new Teneo();
        } else {
            throw new IllegalArgumentException("Unsupported project: " + project);
        }
    }
}