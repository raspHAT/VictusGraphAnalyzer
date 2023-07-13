package com.rasphat.data.portfolio;

import com.rasphat.data.upload.UploadData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuidExtractor {

    private final Map<String, UploadData> guidMap = new HashMap<>();

    public Map<String, UploadData> getGuidMap() {
        return guidMap;
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
        Pattern pattern = Pattern.compile("[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}");
        Matcher matcher = pattern.matcher(rawLine);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}