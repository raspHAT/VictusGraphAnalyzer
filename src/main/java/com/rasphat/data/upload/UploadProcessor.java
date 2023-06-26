package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.List;

public interface UploadProcessor {

    String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;

    List<UploadData> processUploadData(MultipartFile multipartFile);

}