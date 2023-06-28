package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UploadProcessor {
    List<UploadData> processUploadData(MultipartFile multipartFile);
}