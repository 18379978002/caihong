package com.caipiao.modules.oss.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.oss.entity.VerifiCationTag;
import com.caipiao.modules.oss.service.SmsService;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class ITencentSmsServiceImpl implements SmsService {
    public final static String KEY_PREFIX_SMS = "t_sms";
    public final static String KEY_PREFIX_SMS_STATU = "t_sms_s";
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private SmsClient smsClient;

    @Resource
    private Snowflake snowflake;

    @Override
    public String sendVerificationCode(VerifiCationTag verifiCationTag, String phoneNum) {
        String verificationCode = String.valueOf(snowflake.nextId()).substring(0, 6);
        sendVerificationCode(verifiCationTag,phoneNum, verificationCode);
        return verificationCode;
    }

    @Override
    public Boolean sendVerificationCode(VerifiCationTag verifiCationTag, String phoneNum, String verificationCode) {
        String status = (String) redisComponent.hget(KEY_PREFIX_SMS_STATU + verifiCationTag.name()+"_s", phoneNum);
        if(StringUtils.isNotBlank(status)){
            throw new RRException("5分钟内请不要重复发送验证码!");
        }
        redisComponent.hset(KEY_PREFIX_SMS_STATU + verifiCationTag.name(), phoneNum,"is_send",60*5);
        redisComponent.hset(KEY_PREFIX_SMS +verifiCationTag.name(), phoneNum,verificationCode,60*5);
        return sendSms(phoneNum, "1748426", verificationCode);
    }

    @Override
    public String queryVerificationCode(VerifiCationTag verifiCationTag, String phoneNum) {
        String verificationCode = (String) redisComponent.hget(KEY_PREFIX_SMS+verifiCationTag.name(), phoneNum);
        redisComponent.hset(verifiCationTag.name(), phoneNum,verificationCode,1);
        return verificationCode;
    }

    @Override
    public Boolean sendSms(String phoneNum, String templateCode, String templateParam) {
        SendSmsRequest req = new SendSmsRequest();
        req.setPhoneNumberSet(new String[]{phoneNum});
        req.setTemplateId(templateCode);
        req.setSignName("江西熠彩大数据发展");
        req.setTemplateParamSet(new String[]{templateParam});
        req.setSmsSdkAppId("1400807798");
        // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
        try {
            SendSmsResponse resp = smsClient.SendSms(req);
            return "Ok".equals(resp.getSendStatusSet()[0].getCode());
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }
}
