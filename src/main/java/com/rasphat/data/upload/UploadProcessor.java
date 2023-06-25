package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface UploadProcessor {

    final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;

    List<UploadData> dataList = null;

    UploadData processUploadData(String  project, MultipartFile file);
}
