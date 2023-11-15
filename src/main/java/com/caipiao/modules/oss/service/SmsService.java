package com.caipiao.modules.oss.service;

import com.caipiao.modules.oss.entity.VerifiCationTag;

public interface SmsService {


    String sendVerificationCode(VerifiCationTag verifiCationTag,String phoneNum);

    /**
     * @param phoneNum
     * @param verificationCode
     * @param verifiCationTag
     * @return
     */
    Boolean sendVerificationCode(VerifiCationTag verifiCationTag,String phoneNum, String verificationCode);

    String queryVerificationCode(VerifiCationTag verifiCationTag,String phoneNum);

    /**
     * @param phoneNum
     * @param templateCode
     * @param templateParam
     * @return
     */
    Boolean sendSms(String phoneNum, String templateCode, String templateParam);
}
