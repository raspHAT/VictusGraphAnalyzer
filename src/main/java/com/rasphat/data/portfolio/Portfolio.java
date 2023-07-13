package com.rasphat.data.portfolio;

import com.rasphat.data.upload.Upload;
import com.rasphat.data.upload.UploadData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Portfolio {

    private final GuidExtractor guidExtractor = new GuidExtractor();
    // private final Map<String, UploadData> guidMap = getGuidMap();
    private final Map<String, UploadData> guidMap = new HashMap<>();

    public Map<String, UploadData> getGuidMap() {
        return guidMap;
    }

    public void createGuidMap() {
        processUploadDataList(Upload.getUploadDataList());

        // Print the map
        for (Map.Entry<String, UploadData> entry : getGuidMap().entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }


    public void processUploadDataList(List<UploadData> uploadDataList) {
        for (UploadData uploadData : uploadDataList) {
            String guid = extractGuid(uploadData.getRawLine());
            if (guid != null) {
                guidMap.put(guid, uploadData);
            }
        }
    }

    private String extractGuid(String rawLine) {
        rawLine = rawLine.toLowerCase();
        Pattern pattern = Pattern.compile("[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}");
        Matcher matcher = pattern.matcher(rawLine);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
