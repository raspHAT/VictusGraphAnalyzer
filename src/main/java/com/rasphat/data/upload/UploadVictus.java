package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadVictus extends Upload implements UploadProcessor {

    private final String NAME_OF_PROPERTY = "app."+UploadType.VICTUS.name();
    private final Logger logger = LoggerFactory.getLogger(UploadVictus.class);



    @Override
    public List<UploadData> processUploadData(MultipartFile multipartFile) {

        logger.info(UploadVictus.class.getName());

        //List<UploadData> uploadDataList = new ArrayList<>();

        extractZip(multipartFile, getPasswordFromProperty(NAME_OF_PROPERTY));

        try {
            uploadDataList = processFiles();

        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteTempDirectory();

        return uploadDataList;
    }











}