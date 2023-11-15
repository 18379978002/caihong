package com.caipiao.modules.oss.service;

import com.caipiao.modules.oss.entity.VerifiCationTag;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(value = SpringRunner.class)
@SpringBootTest
@Slf4j
public class SmsServiceTest {
    @Resource
    private SmsService smsService;

    @Test
    public void sendVerificationCode() {
        String verificationCode = smsService.sendVerificationCode(VerifiCationTag.LOGIN, "15797736442");
        assert verificationCode != null;
    }

    @Test
    public void queryVerificationCode() {
        String code = smsService.queryVerificationCode(VerifiCationTag.LOGIN, "15797736442");
        log.info("code {}",code);
        assert code != null;
    }
}