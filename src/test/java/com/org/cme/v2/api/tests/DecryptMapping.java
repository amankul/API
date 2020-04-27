package com.org.cme.v2.api.tests;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.unzip.UnzipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
public class DecryptMapping {
    public static void main(String[] args) throws ZipException, IOException {
        extractZip4J("Source", "yp7eUJTEk0/FhQhJcr9EdQ5hLICQi5kOZq8/gAZrlfc=","/Users/akulkarni/Downloads");
    }

    public static final int BUFFER_SIZE = 1024;
    public static void extractZip4J(final String srcPath, final String password,
                                    final String destPath) throws ZipException, IOException {
        if (srcPath == null || password == null || destPath == null) {
            throw new IOException("One or more parameters was null");
        }
        ZipInputStream zis;
        OutputStream os;
        ZipFile zipFile = new ZipFile(srcPath);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password);
        }
        List fileHeaderList = zipFile.getFileHeaders();
        for (int i = 0; i < fileHeaderList.size(); i++) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
            if (fileHeader != null) {
                String outPath = destPath + File.separator + fileHeader.getFileName();
                File outFile = new File(outPath);
                if (fileHeader.isDirectory()) {
                    outFile.mkdirs();
                    continue;
                }
                File parentDir = outFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                zis = zipFile.getInputStream(fileHeader);
                os = new FileOutputStream(outFile);
                int readLen;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((readLen = zis.read(buffer)) != -1) {
                    os.write(buffer, 0, readLen);
                }
                os.close();
                zis.close();
                UnzipUtil.applyFileAttributes(fileHeader, outFile);
            }
        }
    }
}