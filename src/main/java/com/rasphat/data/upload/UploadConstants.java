package com.rasphat.data.upload;

import java.io.File;

/**
 * The UploadConstants class provides constant values used in the Upload component.
 */
public class UploadConstants {

    /**
     * The temporary directory path for storing uploaded files.
     */
    public static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir") + "GRAPH_ANALYZER" + File.separator;

    /**
     * The temporary directory path for storing uploaded files.
     */
    public static final String TEMP_DIR_PATH_EXTRACTED_DATA = System.getProperty("java.io.tmpdir") + "GRAPH_ANALYZER" + File.separator + "EXTRACTED_DATA" + File.separator;

    /**
     * The filename for combined logs.
     */
    public static final String COMBINED_LOGS = "COMBINED_LOGS.txt";

    /**
     * The temporary folder for combined logs.
     */
    public static final String TEMP_DIR_COMBINED_LOGS = TEMP_DIR_PATH + COMBINED_LOGS;

}
