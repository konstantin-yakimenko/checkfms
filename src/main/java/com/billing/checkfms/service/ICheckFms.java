package com.billing.checkfms.service;

import com.billing.checkfms.types.Result;

public interface ICheckFms {

    Result check(final String ser, final String num);
}
