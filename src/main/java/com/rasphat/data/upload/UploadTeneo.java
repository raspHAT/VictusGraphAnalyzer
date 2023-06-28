package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public class UploadTeneo extends Upload implements UploadProcessor {

    private final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;

    private final String NAME_OF_PROPERTY = "app."+UploadType.TENEO.name();

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        System.out.println(TEMP_DIR_PATH);
        System.out.println("TENEO PASSWORD: "+ password);
        return null;
    }
}