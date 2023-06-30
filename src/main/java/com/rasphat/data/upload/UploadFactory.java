package com.rasphat.data.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadFactory {

    private final Logger logger = LoggerFactory.getLogger(UploadFactory.class);

    public UploadProcessor getUploadProcessor(String project) throws Exception {
        try {
            if (project.equals(UploadType.VICTUS.name())) {
                return new UploadVictus();
            } else if (project.equals(UploadType.TENEO.name())) {
                return new UploadTeneo();
            } else if (project.equals(UploadType.ZDW3.name())) {
                return new UploadZdw3();
            } else if (project.equals(UploadType.TENEO_TREATMENTS.name())) {
                return new UploadTeneoTreatments();
            } else {
                return new UploadProjectUnknown();
            }
        } catch (Exception e) { // You need to specify what kind of Exception you're catching
            throw new Exception("Error processing upload for project " + project + " & " +  e + " from type: " + UploadType.UNKNOWN.name()); // or you can rethrow the exception depending on your use case
        }
    }
}
