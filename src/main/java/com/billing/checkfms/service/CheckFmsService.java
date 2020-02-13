package com.billing.checkfms.service;

import com.billing.checkfms.file.FileUpload;
import com.billing.checkfms.types.Result;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CheckFmsService implements ICheckFms {

    private static final Logger logger = LoggerFactory.getLogger(CheckFmsService.class);

    private final FileUpload fileUpload;
    private final IFindPassport findPassport;

    @Autowired
    public CheckFmsService(FileUpload fileUpload,
//                           @Qualifier("findGrep") IFindPassport findPassport
                           @Qualifier("findLegacy") IFindPassport findPassport
//                           @Qualifier("findSet") IFindPassport findPassport
//                           @Qualifier("findSetOfSet") IFindPassport findPassport
    ) {
        this.fileUpload = fileUpload;
        this.findPassport = findPassport;
    }

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
        fileUpload.downloadPassportData();
        return !findPassport.find(ser, num);
    }

}
