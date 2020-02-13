package com.billing.checkfms.file;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
public class FileUpload {

    private static final Logger logger = LoggerFactory.getLogger(FileUpload.class);

    @Value("${file.archive}")
    private String archiveFile;

    @Value("${file.uncompress}")
    private String uncompressFile;

    @Value("${black-passport-url}")
    private String blackPassportUrl;

    public synchronized void downloadPassportData() {
        //проверка сущестования файла
        File blackFile = new File(archiveFile);
        File csvFile = new File(uncompressFile);

        //если файла нет, то загрузка нового
        if (!blackFile.exists()) {
            reloadFile(blackPassportUrl, archiveFile);
        }
        if (!csvFile.exists()) {
            uncompress();
        }
    }

    private void uncompress() {
        try {
            logger.info("Uncompress file");
            FileInputStream in = new FileInputStream(archiveFile);
            FileOutputStream out = new FileOutputStream(uncompressFile);
            BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
            final byte[] buffer = new byte[1024 * 1024];
            int n = 0;
            while (-1 != (n = bzIn.read(buffer))) {
                out.write(buffer, 0, n);
            }
            out.close();
            bzIn.close();
            logger.info("File was uncompressed");
        } catch (IOException e) {
            logger.error("Error: ", e);
        }
    }

    private File reloadFile(String urlSource, String localFile) {
        File local = new File(localFile);
        if (local.exists() && !local.delete())
            throw new RuntimeException("Не могу удалить файл с паспортными данными.");

        try {
            logger.info("Start load file");
            File dir = getDir(localFile);
            if (dir != null && !dir.exists())
                dir.mkdirs();

            local.createNewFile();

            ReadableByteChannel rbc = Channels.newChannel(new URL(urlSource).openStream());
            FileOutputStream fos = new FileOutputStream(localFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            logger.info("File was uploaded");
        } catch (IOException e) {
            logger.error("Error: ", e);
        }

        return new File(archiveFile);
    }

    private File getDir(String localFile) {
        if (localFile == null)
            return null;

        int pos = localFile.lastIndexOf("/");
        if (pos <= 0)
            return null;

        return new File(localFile.substring(0, pos));
    }

}
