package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class UploadZdw3 implements UploadProcessor {

    private final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;


    @Override
    public UploadData processUploadData(String project, MultipartFile file) {
        return null;
    }
}
