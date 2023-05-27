package com.rasphat.zipExtractor;

import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;

public interface ZipHandler {
    void handleZip(File File, String path, char[] password) throws IOException;
    void handleException(ZipException e);
}