package com.billing.checkfms.service.set;

import com.billing.checkfms.file.ReloadCache;
import com.billing.checkfms.service.IFindPassport;
import com.billing.checkfms.service.IUpdateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class FindSet implements IFindPassport, IUpdateCache {

    private final Set<String> cache = new HashSet<>();
//    private final Set<String> cache = new TreeSet<>();

    @Autowired
    private ReloadCache reloadCache;

    @Override
    public boolean find(String ser, String num) {
        updatePassportData();
        String key = ser + "|" + num;
        return cache.contains(key);
    }

    private synchronized void updatePassportData() {
        if (cache.isEmpty()) {
            reloadCache.reloadCache(this);
        }
    }

    @Override
    public int load(String ser, String num) {
        cache.add(ser + "|" + num);
        return 1;
    }

    @Override
    public void sort() {
    }
}
