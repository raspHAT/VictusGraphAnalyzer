package com.rasphat.UploadConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipToInputStreamConverter {
    public InputStream convertToInputStream(byte[] zipData, String filePath) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipData);
        ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);

        ZipEntry desiredEntry = null;
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (entry.getName().equals(filePath)) {
                desiredEntry = entry;
                break;
            }
            zipInputStream.closeEntry();
        }

        if (desiredEntry != null) {
            return new ZipEntryInputStream(zipInputStream, desiredEntry);
        } else {
            zipInputStream.close();
            return null;
        }
    }

    private static class ZipEntryInputStream extends InputStream {
        private final ZipInputStream zipInputStream;
        private final ZipEntry zipEntry;

        public ZipEntryInputStream(ZipInputStream zipInputStream, ZipEntry zipEntry) {
            this.zipInputStream = zipInputStream;
            this.zipEntry = zipEntry;
        }

        @Override
        public int read() throws IOException {
            return zipInputStream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return zipInputStream.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return zipInputStream.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return zipInputStream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return zipInputStream.available();
        }

        @Override
        public void close() throws IOException {
            zipInputStream.closeEntry();
            zipInputStream.close();
        }

        @Override
        public void mark(int readlimit) {
            zipInputStream.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            zipInputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return zipInputStream.markSupported();
        }
    }
}
