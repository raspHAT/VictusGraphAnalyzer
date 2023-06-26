package com.rasphat.data.upload;

import com.rasphat.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.zip.ZipInputStream;

public abstract class Upload {

    public static UploadProcessor getUploadProcessor(String project) {

        if (project.equals(UploadType.VICTUS.name())) {
            return new UploadVictus();
        } else if (project.equals(UploadType.TENEO.name())) {
            return new UploadTeneo();
        } else if (project.equals(UploadType.ZDW3.name())) {
            return new UploadZdw3();
        } else if (project.equals(UploadType.TENEO_TREATMENTS.name())) {
            return new UploadTeneoTreatments();
        } else {
            throw new IllegalArgumentException("Unsupported project: " + project);
        }
    }

    protected String getPasswordFromProperty(String nameOfProperty) {
        String passwordVictus;
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
            passwordVictus = properties.getProperty(nameOfProperty);
        } catch (IOException e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        return passwordVictus;
    }

    /**
     * Checks if the provided file is a valid ZIP file.
     *
     * @param file the file to be checked.
     * @return true if the file is a valid ZIP file, false otherwise.
     */
    protected boolean isZipFile(File file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            return zipInputStream.getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }

}