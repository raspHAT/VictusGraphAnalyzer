package com.rasphat.data.portfolio;

import com.rasphat.data.upload.Upload;
import com.rasphat.data.upload.UploadData;


import java.util.Map;

public class PortfolioVictus {


    private final GuidExtractor guidExtractor = new GuidExtractor();

    private final Map<String, UploadData> guidMap = guidExtractor.getGuidMap();

    public void creteGuidMap() {
        guidExtractor.processUploadDataList(Upload.getUploadDataList());

        // Print the map
        for (Map.Entry<String, UploadData> entry : guidExtractor.getGuidMap().entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        System.out.println(guidExtractor.getGuidMap().size());
    }
}
