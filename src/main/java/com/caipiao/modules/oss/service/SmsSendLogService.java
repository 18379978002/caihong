package com.caipiao.modules.oss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.oss.entity.SmsSendLog;
import com.caipiao.modules.oss.entity.req.SendSmsDTO;

import java.util.List;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/12 20:14
 */
public interface SmsSendLogService extends IService<SmsSendLog> {
    /**
     * 根据组别批量发送
     * @param dto
     * @param companyId
     * @return
     */
    Boolean batchSend(SendSmsDTO dto, String companyId);
    Boolean sendCodeSms(String phone, String templateCode, JSONObject params, String companyId);

    /**
     * 根据号码批量发送
     * @param phoneNumbers
     * @param idCardSendCode
     * @param companyId
     * @return
     */
    Boolean batchSend(List<String> phoneNumbers, String idCardSendCode, String companyId);
}
