package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

public interface UploadProcessor {
    UploadData processUploadData(String  project, MultipartFile file);
}
