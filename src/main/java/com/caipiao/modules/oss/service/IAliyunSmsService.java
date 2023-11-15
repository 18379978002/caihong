package com.caipiao.modules.oss.service;

import com.aliyuncs.CommonResponse;

import java.util.List;

/**
 * ego-maker
 *
 * @author : xiaoyinandan
 * @date : 2020/11/25 16:57
 */
public interface IAliyunSmsService {

    /**
     * @param templateType  0：验证码。 1：短信通知。 2：推广短信。 3：国际/港澳台消息
     * @param templateName
     * @param templateContent  您正在申请手机注册，验证码为：${code}，5分钟内有效！
     * @return
     *
     *
     * {"data":"{\"TemplateCode\":\"SMS_215116067\",\"Message\":\"OK\",\"RequestId\":\"3CE9FAC1-DCE1-4977-A54A-8015E14A09CD\",\"Code\":\"OK\"}","httpResponse":{"encoding":"UTF-8","headers":{"Access-Control-Allow-Origin":"*","x-acs-request-id":"3CE9FAC1-DCE1-4977-A54A-8015E14A09CD","Access-Control-Allow-Methods":"POST, GET, OPTIONS","Connection":"keep-alive","Access-Control-Max-Age":"172800","Content-Length":"110","Access-Control-Allow-Headers":"X-Requested-With, X-Sequence, _aop_secret, _aop_signature","Date":"Sat, 10 Apr 2021 11:56:03 GMT","Content-Type":"application/json;charset=utf-8"},"httpContent":"eyJUZW1wbGF0ZUNvZGUiOiJTTVNfMjE1MTE2MDY3IiwiTWVzc2FnZSI6Ik9LIiwiUmVxdWVzdElkIjoiM0NFOUZBQzEtRENFMS00OTc3LUE1NEEtODAxNUUxNEEwOUNEIiwiQ29kZSI6Ik9LIn0=","httpContentString":"{\"TemplateCode\":\"SMS_215116067\",\"Message\":\"OK\",\"RequestId\":\"3CE9FAC1-DCE1-4977-A54A-8015E14A09CD\",\"Code\":\"OK\"}","httpContentType":"JSON","status":200,"success":true,"url":"http://dysmsapi.aliyuncs.com/"},"httpStatus":200}
     *
     */
    CommonResponse addSmsTemplate(String templateType, String templateName, String templateContent, String remark);

    /**
     * @param templateType  0：验证码。 1：短信通知。 2：推广短信。 3：国际/港澳台消息
     * @param templateName
     * @param templateContent  您正在申请手机注册，验证码为：${code}，5分钟内有效！
     * @return
     */
    CommonResponse modifySmsTemplate(String templateType, String templateName, String templateContent, String templateCode, String remark);

    /**
     * {"code":"1111"}
     * @param phoneNum
     * @param templateCode
     * @param templateParam
     * @return
     */
    CommonResponse sendSms(String phoneNum, String templateCode, String templateParam);

    /**
     * 查询模板
     * @param templateCode
     * @return
     *
     * {"data":"{\"TemplateCode\":\"SMS_215116067\",\"Message\":\"OK\",\"RequestId\":\"D23E1FEF-EEEA-45FC-87DC-4DFABD9B82F5\",\"TemplateContent\":\"您的工商注册，业务号码${bizId},申请已成功通过，详情登录公众号查看\",\"TemplateName\":\"工商注册成功通知\",\"TemplateType\":1,\"Code\":\"OK\",\"CreateDate\":\"2021-04-10 19:56:04\",\"Reason\":\"无审批备注\",\"TemplateStatus\":0}","httpResponse":{"encoding":"UTF-8","headers":{"Access-Control-Allow-Origin":"*","x-acs-request-id":"D23E1FEF-EEEA-45FC-87DC-4DFABD9B82F5","Access-Control-Allow-Methods":"POST, GET, OPTIONS","Connection":"keep-alive","Access-Control-Max-Age":"172800","Content-Length":"364","Access-Control-Allow-Headers":"X-Requested-With, X-Sequence, _aop_secret, _aop_signature","Date":"Sat, 10 Apr 2021 11:59:36 GMT","Content-Type":"application/json;charset=utf-8"},"httpContent":"eyJUZW1wbGF0ZUNvZGUiOiJTTVNfMjE1MTE2MDY3IiwiTWVzc2FnZSI6Ik9LIiwiUmVxdWVzdElkIjoiRDIzRTFGRUYtRUVFQS00NUZDLTg3REMtNERGQUJEOUI4MkY1IiwiVGVtcGxhdGVDb250ZW50Ijoi5oKo55qE5bel5ZWG5rOo5YaM77yM5Lia5Yqh5Y+356CBJHtiaXpJZH0s55Sz6K+35bey5oiQ5Yqf6YCa6L+H77yM6K+m5oOF55m75b2V5YWs5LyX5Y+35p+l55yLIiwiVGVtcGxhdGVOYW1lIjoi5bel5ZWG5rOo5YaM5oiQ5Yqf6YCa55+lIiwiVGVtcGxhdGVUeXBlIjoxLCJDb2RlIjoiT0siLCJDcmVhdGVEYXRlIjoiMjAyMS0wNC0xMCAxOTo1NjowNCIsIlJlYXNvbiI6IuaXoOWuoeaJueWkh+azqCIsIlRlbXBsYXRlU3RhdHVzIjowfQ==","httpContentString":"{\"TemplateCode\":\"SMS_215116067\",\"Message\":\"OK\",\"RequestId\":\"D23E1FEF-EEEA-45FC-87DC-4DFABD9B82F5\",\"TemplateContent\":\"您的工商注册，业务号码${bizId},申请已成功通过，详情登录公众号查看\",\"TemplateName\":\"工商注册成功通知\",\"TemplateType\":1,\"Code\":\"OK\",\"CreateDate\":\"2021-04-10 19:56:04\",\"Reason\":\"无审批备注\",\"TemplateStatus\":0}","httpContentType":"JSON","status":200,"success":true,"url":"http://dysmsapi.aliyuncs.com/"},"httpStatus":200}
     */
    CommonResponse querySmsTemplate(String templateCode);

    /**
     * 删除模板
     * @param templateCode
     * @return
     */
    CommonResponse deleteSmsTemplate(String templateCode);


    /**
     * {"code":"1111"}
     * @param phoneNums
     * @param templateCode
     * @param templateParam
     * @return
     */
    CommonResponse sendBatchSms(List<String> phoneNums, String templateCode, String templateParam);

    CommonResponse querySendDetails(String phone, String sendDate, Long curPage, Long pageSize, String bizId);


}
