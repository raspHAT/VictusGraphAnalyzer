package com.rasphat.zipExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ZipExtractor {

    private final Map<String, ZipHandler> handlers = new HashMap<>();
    private static final String tempDirPath = System.getProperty("java.io.tmpdir") + "extractZip/";

    public ZipExtractor() {
        handlers.put("pipiskamanakonja", new VictusZipHandler());
        handlers.put("password2", new ZDW3ZipHandler());
    }

    public void extractZip(byte[] bytes, String password) throws IOException {

        File tempZipFile = File.createTempFile("extractZip", ".zip");

        try (FileOutputStream fos = new FileOutputStream(tempZipFile)) {
            fos.write(bytes);
        }

        try {
            ZipHandler handler = handlers.get(password);
            if (handler != null) {

                handler.handleZip(tempZipFile, tempDirPath, password.toCharArray());
                // Only delete the directory if the handling was successful
                deleteDirectory(new File(tempDirPath));
            } else {
                throw new IllegalArgumentException("No handler for password " + password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!tempZipFile.delete()) {
                tempZipFile.deleteOnExit();
            }
        }
    }

    public static void deleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        if (!directoryToBeDeleted.delete()) {
            throw new IOException("Failed to delete " + directoryToBeDeleted);
        }
    }
}