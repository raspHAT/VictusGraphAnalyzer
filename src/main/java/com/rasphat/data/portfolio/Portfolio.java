package com.rasphat.data.portfolio;

import com.rasphat.data.upload.Upload;
import com.rasphat.data.upload.UploadData;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Portfolio is an abstract class that represents a collection of upload data with associated GUIDs.
 * It provides methods to create and manage a GUID map based on the upload data.
 */
public abstract class Portfolio {

    /**
     * Map to store the GUIDs as keys and corresponding UploadData objects as values.
     */
    private final Map<String, UploadData> guidMap = new HashMap<>();

    /**
     * Returns the GUID map.
     *
     * @return The GUID map.
     */
    public Map<String, UploadData> getGuidMap() {
        return guidMap;
    }

    /**
     * Creates the GUID map by processing the upload data list.
     * The GUIDs are extracted from the raw lines of the upload data and associated with the respective UploadData objects.
     * Prints the key-value pairs in the map.
     */
    public void createGuidMap() {
        processUploadDataList(Upload.getUploadDataList());

        // Print the map
        for (Map.Entry<String, UploadData> entry : getGuidMap().entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    /**
     * Processes the upload data list and populates the GUID map.
     * The GUIDs are extracted from the raw lines of the upload data and associated with the respective UploadData objects.
     *
     * @param uploadDataList The list of upload data to be processed.
     */
    public void processUploadDataList(List<UploadData> uploadDataList) {
        for (UploadData uploadData : uploadDataList) {
            String guid = extractGuid(uploadData.getRawLine());
            if (guid != null) {
                guidMap.put(guid, uploadData);
            }
        }
    }

    /**
     * Extracts the GUID from the given raw line.
     * The raw line is converted to lowercase and searched for a GUID pattern.
     *
     * @param rawLine The raw line from which the GUID is to be extracted.
     * @return The extracted GUID, or null if no GUID is found.
     */
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