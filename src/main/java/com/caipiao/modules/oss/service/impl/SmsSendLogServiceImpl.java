package com.caipiao.modules.oss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.CollectionUtils;
import com.caipiao.modules.oss.dao.SmsSendLogDao;
import com.caipiao.modules.oss.dao.SmsTemplateDao;
import com.caipiao.modules.oss.entity.SmsSendLog;
import com.caipiao.modules.oss.entity.SmsTemplate;
import com.caipiao.modules.oss.entity.req.SendSmsDTO;
import com.caipiao.modules.oss.service.IAliyunSmsService;
import com.caipiao.modules.oss.service.SmsSendLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/12 20:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsSendLogServiceImpl extends ServiceImpl<SmsSendLogDao, SmsSendLog> implements SmsSendLogService {
    private final IAliyunSmsService aliyunSmsService;
    private final SmsTemplateDao smsTemplateDao;

    @Override
    public Boolean batchSend(SendSmsDTO dto, String companyId) {

        SmsTemplate smsTemplate = smsTemplateDao.selectById(dto.getTemplateCode());

        if(null == smsTemplate || smsTemplate.getTemplateStatus() != 1 || !companyId.equals(smsTemplate.getCompanyId())){
            throw new RRException("不存在的模板或者模板未通过审核或者没有权限");
        }



        return false;
    }

    @Override
    public Boolean sendCodeSms(String phone, String templateCode, JSONObject params, String companyId) {

        SmsTemplate template = smsTemplateDao.selectById(templateCode);

        if(null == template || template.getTemplateStatus() != 1 || !companyId.equals(template.getCompanyId())){
            throw new RRException("不存在的模板或者模板未通过审核或者没有权限");
        }

        CommonResponse commonResponse = aliyunSmsService.sendSms(phone, templateCode, JSON.toJSONString(params));

        JSONObject object = JSONObject.parseObject(commonResponse.getData());
        String code = object.getString("Code");
        if(code.equalsIgnoreCase("OK")){
            //成功发送
            String bizId = object.getString("BizId");

            this.save(getLog(bizId, phone, templateCode, template, companyId));
            return true;

        }else{
            throw new RRException(object.getString("Message"));
        }
    }

    @Override
    public Boolean batchSend(List<String> phoneNumbers, String idCardSendCode, String companyId) {
        SmsTemplate smsTemplate = smsTemplateDao.selectById(idCardSendCode);

        if(null == smsTemplate || smsTemplate.getTemplateStatus() != 1 || !companyId.equals(smsTemplate.getCompanyId())){
            throw new RRException("不存在的模板或者模板未通过审核或者没有权限");
        }


        if(CollectionUtils.isNotEmpty(phoneNumbers)){
            CommonResponse commonResponse = aliyunSmsService.sendBatchSms(phoneNumbers, idCardSendCode, null);

            JSONObject object = JSONObject.parseObject(commonResponse.getData());
            String code = object.getString("Code");
            if(code.equalsIgnoreCase("OK")){
                //成功发送
                String bizId = object.getString("BizId");

                List<SmsSendLog> logs = new ArrayList<>();

                for (String phoneNumber : phoneNumbers) {
                    SmsSendLog log = getLog(bizId, phoneNumber, idCardSendCode, smsTemplate, companyId);
                    logs.add(log);
                }

                this.saveBatch(logs);

                return true;

            }else{
                throw new RRException(object.getString("Message"));
            }
        }



        return false;
    }


    private SmsSendLog getLog(String bizId, String phone, String templateCode, SmsTemplate template, String companyId){
        SmsSendLog log = new SmsSendLog();
        log.setBizId(bizId);
        log.setPhoneNumber(phone);
        log.setSendState(1);
        log.setSendTemplateCode(templateCode);
        log.setSendContent(template.getTemplateContent());
        log.setSendTemplateName(template.getTemplateName());
        log.setSendTime(new Date());
        log.setCompanyId(companyId);
        return log;
    }
}
