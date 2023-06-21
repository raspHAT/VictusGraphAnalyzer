package com.rasphat.data.factories.upload.factory;

import com.rasphat.data.factories.upload.factory.Upload;
import com.rasphat.data.factories.upload.factory.AbstractUploadProcessor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class Victus extends AbstractUploadProcessor {
    @Override
    public void processFile(String project, MultipartFile file) throws IOException {
        // Implementation specific to Projrct Victus
        Upload upload = new Upload();
        upload.setProject(project);
        setFileContent(upload, file);
        // Add additional Project A specific processing if needed
    }
}