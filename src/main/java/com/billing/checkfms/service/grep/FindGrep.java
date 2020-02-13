package com.billing.checkfms.service.grep;

import com.billing.checkfms.service.IFindPassport;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Component
public class FindGrep implements IFindPassport {

    private static final Logger logger = LoggerFactory.getLogger(FindGrep.class);

    @Value("${file.uncompress}")
    private String uncompressFile;

    public boolean find(String ser, String num) {
        try {
            String row = ser + "," + num;
            logger.info("Find passport {} by grep", row);

            Process p1 = Runtime.getRuntime().exec(new String[]{"grep", row, uncompressFile});
            List<String> result = IOUtils.readLines(p1.getInputStream(), Charset.defaultCharset());

            logger.info("result grep: {}", result);
            boolean isFind = result.contains(row);
            logger.info("Result found passport {}: {}", row, isFind);
            return isFind;
        } catch (IOException e) {
            logger.error("Error: ", e);
            return false;
        }
    }
}
