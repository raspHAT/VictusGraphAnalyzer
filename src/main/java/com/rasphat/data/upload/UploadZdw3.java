package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public class UploadZdw3  extends Upload implements UploadProcessor {

    private final String NAME_OF_PROPERTY = "app."+UploadType.ZDW3.name();

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        String password = getPasswordFromProperty(NAME_OF_PROPERTY);

        System.out.println(TEMP_DIR_PATH);
        System.out.println("TENEO PASSWORD: "+ password);
        return null;
    }
}
