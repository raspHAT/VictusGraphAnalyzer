package com.rasphat.data.upload;

import java.util.List;

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
     * @return A list of UploadData objects representing the processed upload data.
     */
    List<UploadData> processUploadData(MultipartFile multipartFile);
}
