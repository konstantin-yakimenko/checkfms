package com.billing.checkfms.service;

import com.billing.checkfms.types.Result;
import com.csvreader.CsvReader;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

@Service
public class CheckFmsService implements ICheckFms {

    private static final Logger logger = LoggerFactory.getLogger(CheckFmsService.class);

    private static final String LOCAL_FILE = "resources/blackpassports.csv.bz2";
    private static final String BLACK_PASSPORT_URL = "https://guvm.mvd.ru/upload/expired-passports/list_of_expired_passports.csv.bz2";


    @Override
    public Result check(String ser, String num) {
        if (Strings.isEmpty(ser) || Strings.isEmpty(num)) {
            return Result.INCORRECT_PASSPORT;
        }
        return isCorrect(ser, num)
                ? Result.OK
                : Result.IN_BLACK_LIST;
    }


    private boolean isCorrect(String ser, String num) {
        updatePassportData();
        // todo каким-то образом надо проверит паспортные данныеø
        return true;
    }

    private synchronized void updatePassportData() {
        //проверка сущестования файла
        File blackFile = new File(LOCAL_FILE);

        //если файла нет, то загрузка нового
        if (!blackFile.exists()) {
            blackFile = reloadFile(BLACK_PASSPORT_URL, LOCAL_FILE);
            reloadCache(blackFile);
        }
    }

    private void reloadCache(File blackFile) {
        try {
            logger.info("unarchive file");
            BZip2CompressorInputStream is = new BZip2CompressorInputStream(new FileInputStream(blackFile));

            int skip = 0;
            int total = 0;
            CsvReader in = new CsvReader(is, Charset.forName("UTF-8"));
            while (in.readRecord()) {
                String strSeries = in.get(0);
                String strNuber = in.get(1);

                total++;
                if (total % 1_000_000 == 0) { // логируем каждый миллион прочитанных строк
                    logger.info("Загружено {} записей", total);
                }
            }
            is.close();

            logger.info("Обновлён кеш с паспортными данными. Загружено " + total + " записей.");
        } catch (Exception e) {
            logger.error("Error: ", e);
        } finally {
            logger.info("Загрузка файла завершена");
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

        return new File(LOCAL_FILE);
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
