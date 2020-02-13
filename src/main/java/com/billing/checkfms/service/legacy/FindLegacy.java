package com.billing.checkfms.service.legacy;

import com.billing.checkfms.file.ReloadCache;
import com.billing.checkfms.service.IFindPassport;
import com.billing.checkfms.service.IUpdateCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindLegacy extends NumberCache implements IFindPassport, IUpdateCache {

    private static final Logger logger = LoggerFactory.getLogger(FindLegacy.class);

    @Autowired
    private ReloadCache reloadCache;

    public FindLegacy() {
        super(1024 * 1024, 1.33f);
    }


    @Override
    public boolean find(String ser, String num) {
        long code = getPassportCode(ser, num);
        if (code < 0) {
            throw new IllegalArgumentException("Неверные серия и дата");
        }
        return contains(code);
    }

    @Override
    public boolean contains(long x) {
        updatePassportData();

        return super.contains(x);
    }


    /**
     * Сформировать код пасспорта в кеше.
     *
     * @return код пасспорта или ошибка:
     * -1 - не заданы параметры,
     * -2 - не являются числом,
     * -3 - слишком большое число.
     */
    private long getPassportCode(String series, String number) {
        if (series == null || number == null || series.isEmpty() || number.isEmpty())
            return -1;
        try {
            long x = Integer.parseInt(series);
            long y = Integer.parseInt(number);
            return series.length() > 5 || number.length() > 7 ? -3 : x * 10000000 + y;
        } catch (NumberFormatException e) {
            logger.info("{}:{}", series, number);
            return -2;
        }
    }

    private synchronized void updatePassportData() {
        if (isEmpty()) {
            reloadCache.reloadCache(this);
        }
    }

    @Override
    public int load(String ser, String num) {
        long code = getPassportCode(ser, num);
        if (code >= 0) {
            addItem(code);
            return 1;
        }
        return 0;
    }

    @Override
    public void sort() {
        super.sort();
    }


}
