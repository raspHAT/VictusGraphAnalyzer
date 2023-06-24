package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

public class Upload implements UploadProcessor{

    @Override
    public UploadData processUploadData(String project, MultipartFile file) {
        UploadProcessor uploadProcessor = getUploadProcessor(project);
        return null;
    }

    private static UploadProcessor getUploadProcessor(String project) {
        if (project.equals(UploadType.VICTUS.name())) {
            return new Victus();
        } else if (project.equals(UploadType.TENEO.name())) {
            return new Teneo();
        } else if (project.equals(UploadType.ZDW3.name())) {
            return new Zdw3();
        } else if (project.equals(UploadType.TENEO_TREATMENTS.name())) {
            return new TeneoTreatments();
        } else {
            throw new IllegalArgumentException("Unsupported project: " + project);
        }
    }


}