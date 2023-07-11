package com.rasphat.data.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * The UploadProcessor interface represents a component responsible for processing
 * upload data from a MultipartFile.
 */
public interface UploadProcessor {

    /**
     * Processes the upload data from the provided MultipartFile and returns a list of UploadData objects.
     *
     * @param multipartFile The MultipartFile containing the upload data.
     */
    void processUploadData(MultipartFile multipartFile);
}

