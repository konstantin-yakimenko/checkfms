package com.billing.checkfms.file;

import com.billing.checkfms.service.IUpdateCache;
import com.csvreader.CsvReader;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ReloadCache {

    private static final Logger logger = LoggerFactory.getLogger(ReloadCache.class);

    @Value("${file.archive}")
    private String archiveFile;

    public void reloadCache(IUpdateCache updateCache) {
        try {
            logger.info("unarchive file");
            File blackFile = new File(archiveFile);

            BZip2CompressorInputStream is = new BZip2CompressorInputStream(new FileInputStream(blackFile));

            int total = 0;
            CsvReader in = new CsvReader(is, StandardCharsets.UTF_8);
            while (in.readRecord()) {
                String strSeries = in.get(0);
                String strNumber = in.get(1);

                total += updateCache.load(strSeries, strNumber);

                if (total % 1_000_000 == 0) { // логируем каждый миллион прочитанных строк
                    logger.info("Загружено {} записей", total);
                }
            }
            is.close();

            updateCache.sort();

            logger.info("Обновлён кеш с паспортными данными. Загружено " + total + " записей.");
        } catch (Exception e) {
            logger.error("Error: ", e);
        } finally {
            logger.info("Загрузка файла завершена");
        }
    }

}
