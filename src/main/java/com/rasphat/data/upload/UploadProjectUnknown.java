package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class UploadProjectUnknown extends Upload implements UploadProcessor{

    private final Logger logger = LoggerFactory.getLogger(UploadVictus.class);
    private final String NAME_OF_PROPERTY = "app."+UploadType.UNKNOWN.name();

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {
        logger.info(UploadProjectUnknown.class.getName() + " previous project from type: " + NAME_OF_PROPERTY);
        return new ArrayList<>();
    }

}
