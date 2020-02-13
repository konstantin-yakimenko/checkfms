package com.billing.checkfms.service.setofset;

import com.billing.checkfms.file.ReloadCache;
import com.billing.checkfms.service.IFindPassport;
import com.billing.checkfms.service.IUpdateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class FindSetOfSet implements IFindPassport, IUpdateCache {

    private final Map<String, Set<String>> cache = new HashMap<>();

    @Autowired
    private ReloadCache reloadCache;

    @Override
    public boolean find(String ser, String num) {
        updatePassportData();

        Set<String> numbers = cache.get(ser);
        if (numbers == null || numbers.isEmpty()) {
            return false;
        }
        return numbers.contains(num);
    }

    private synchronized void updatePassportData() {
        if (cache.isEmpty()) {
            reloadCache.reloadCache(this);
        }
    }

    @Override
    public int load(String ser, String num) {
        cache.putIfAbsent(ser, new HashSet<>());
        cache.get(ser).add(num);
        return 1;
    }

    @Override
    public void sort() {
    }
}
