package com.billing.checkfms.controller;

import com.billing.checkfms.service.ICheckFms;
import com.billing.checkfms.types.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
public class CheckFmsController {

    private static final Logger logger = LoggerFactory.getLogger(CheckFmsController.class);

    private final ICheckFms service;

    @Autowired
    public CheckFmsController(ICheckFms service) {
        this.service = service;
    }

    @GetMapping("/check")
    public Result check(
            @RequestParam("ser") @NotNull String ser,
            @RequestParam("num") @NotNull String num) {
        logger.info("Check passport by FMS, ser: {}, num: {}", ser, num);

        return service.check(ser, num);
    }
}
