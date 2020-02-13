package com.billing.checkfms.service;

public interface IUpdateCache {

    int load(String ser, String num);

    void sort();
}
