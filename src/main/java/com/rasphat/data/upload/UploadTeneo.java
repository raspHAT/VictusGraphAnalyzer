package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadTeneo extends Upload implements UploadProcessor {

    private final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    private final String NAME_OF_PROPERTY = "app."+UploadType.TENEO.name();

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        List<UploadData> uploadDataList = new ArrayList<>();
        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        return uploadDataList;
    }
}