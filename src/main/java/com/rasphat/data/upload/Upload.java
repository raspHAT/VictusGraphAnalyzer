package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

public class Upload implements UploadProcessor {


    public Upload(String project, MultipartFile file) {
        processUploadData(project, file);
    }

    @Override
    public UploadData processUploadData(String project, MultipartFile file) {
        UploadProcessor uploadProcessor = getUploadProcessor(project);
        return null;
    }

    private UploadProcessor getUploadProcessor(String project) {
        if (project.equals(UploadType.VICTUS.name())) {
            return new UploadVictus();
        } else if (project.equals(UploadType.TENEO.name())) {
            return new UploadTeneo();
        } else if (project.equals(UploadType.ZDW3.name())) {
            return new UploadZdw3();
        } else if (project.equals(UploadType.TENEO_TREATMENTS.name())) {
            return new UploadTeneoTreatments();
        } else {
            throw new IllegalArgumentException("Unsupported project: " + project);
        }
    }


}