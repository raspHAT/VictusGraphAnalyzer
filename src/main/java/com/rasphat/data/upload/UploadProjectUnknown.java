package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadProjectUnknown extends Upload implements UploadProcessor{

    // private final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "VictusGraphAnalyzer" + File.separator;
    private final Logger logger = LoggerFactory.getLogger(UploadVictus.class);
    private final String NAME_OF_PROPERTY = "app."+UploadType.UNKNOWN.name();

    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        logger.info(UploadProjectUnknown.class.getName() + NAME_OF_PROPERTY);

        return new ArrayList<>();
    }

}
