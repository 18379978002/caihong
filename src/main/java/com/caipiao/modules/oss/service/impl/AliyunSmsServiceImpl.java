package com.caipiao.modules.oss.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.oss.config.AliyunSmsProperties;
import com.caipiao.modules.oss.service.IAliyunSmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ego-maker
 *
 * @author : xiaoyinandan
 * @date : 2020/11/25 16:59
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunSmsServiceImpl implements IAliyunSmsService {

    private final AliyunSmsProperties aliyunSmsProperties;

    @Override
    public CommonResponse addSmsTemplate(String templateType, String templateName, String templateContent, String remark) {
        IAcsClient client = this.getAcsClinet();
        CommonRequest request = this.getCommonRequest();

        request.setAction("AddSmsTemplate");
        request.putQueryParameter("RegionId", aliyunSmsProperties.getRegionId());
        request.putQueryParameter("TemplateType", templateType);
        request.putQueryParameter("TemplateName", templateName);
        request.putQueryParameter("TemplateContent", templateContent);
        request.putQueryParameter("Remark", remark);
        request.setVersion("2017-05-25");

        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("返回的数据：{}", JSON.toJSONString(response));
            return response;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CommonResponse modifySmsTemplate(String templateType, String templateName, String templateContent, String templateCode, String remark) {
        IAcsClient client = this.getAcsClinet();
        CommonRequest request = this.getCommonRequest();

        request.setAction("ModifySmsTemplate");
        request.putQueryParameter("RegionId", aliyunSmsProperties.getRegionId());
        request.putQueryParameter("TemplateType", templateType);
        request.putQueryParameter("TemplateName", templateName);
        request.putQueryParameter("TemplateContent", templateContent);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("Remark", remark);
        request.setVersion("2017-05-25");

        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("返回的数据：{}", JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CommonResponse querySmsTemplate(String templateCode) {
        IAcsClient client = this.getAcsClinet();
        CommonRequest request = this.getCommonRequest();

        request.setAction("QuerySmsTemplate");
        request.putQueryParameter("AccessKeyId", aliyunSmsProperties.getAccessKey());
        request.putQueryParameter("TemplateCode", templateCode);
        request.setVersion("2017-05-25");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("返回的数据：{}", JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CommonResponse deleteSmsTemplate(String templateCode) {
        IAcsClient client = this.getAcsClinet();
        CommonRequest request = this.getCommonRequest();

        request.setAction("DeleteSmsTemplate");
        request.putQueryParameter("AccessKeyId", aliyunSmsProperties.getAccessKey());
        request.putQueryParameter("TemplateCode", templateCode);
        request.setVersion("2017-05-25");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("返回的数据：{}", JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CommonResponse sendBatchSms(List<String> phoneNums, String templateCode, String templateParam) {
        IAcsClient client = this.getAcsClinet();
        CommonRequest request = this.getCommonRequest();

        List<String> signNames = new ArrayList<>();

        for(int i = 0; i < phoneNums.size(); i++){
            signNames.add(aliyunSmsProperties.getSignName());
        }

        request.setAction("SendBatchSms");
        request.setVersion("2017-05-25");
        request.putQueryParameter("RegionId", aliyunSmsProperties.getRegionId());
        request.putQueryParameter("PhoneNumberJson", JSON.toJSONString(phoneNums));
        request.putQueryParameter("SignNameJson", JSON.toJSONString(signNames));
        request.putQueryParameter("TemplateCode", templateCode);
        if(StringUtils.isNotEmpty(templateParam)){
            request.putQueryParameter("TemplateParamJson", templateParam);
        }

        try {
            log.debug("短信批量发送参数: {}", JSON.toJSONString(request));
            CommonResponse response = client.getCommonResponse(request);
            log.debug("短信批量发送结果: {}", JSON.toJSONString(response));
            return response;
        } catch (ServerException e) {
            log.error("短信批量发送异常(ServerException):", e);
        } catch (ClientException e) {
            log.error("短信批量发送异常(ClientException):", e);
        }
        return null;
    }

    @Override
    public CommonResponse querySendDetails(String phone, String sendDate, Long curPage, Long pageSize, String bizId) {
        IAcsClient client = this.getAcsClinet();
        CommonRequest request = this.getCommonRequest();
        request.setAction("QuerySendDetails");
        request.setVersion("2017-05-25");
        request.putQueryParameter("RegionId", aliyunSmsProperties.getRegionId());
        request.putQueryParameter("PhoneNumber", phone);
        request.putQueryParameter("SendDate", sendDate);
        request.putQueryParameter("CurrentPage", curPage+"");
        request.putQueryParameter("PageSize", pageSize+"");
        request.putQueryParameter("BizId", bizId);
        try {
            log.debug("短信查询参数: {}", JSON.toJSONString(request));
            CommonResponse response = client.getCommonResponse(request);
            log.debug("短信查询结果: {}", JSON.toJSONString(response));
            return response;
        } catch (ServerException e) {
            log.error("短信查询异常(ServerException):", e);
        } catch (ClientException e) {
            log.error("短信查询异常(ClientException):", e);
        }
        return null;
    }

    @Override
    public CommonResponse sendSms(String phoneNum, String templateCode, String templateParam) {
        IAcsClient client = this.getAcsClinet();
        CommonRequest request = this.getCommonRequest();
        request.setAction("SendSms");
        request.setVersion("2017-05-25");
        request.putQueryParameter("RegionId", aliyunSmsProperties.getRegionId());
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", aliyunSmsProperties.getSignName());
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            log.debug("短信发送参数: {}", JSON.toJSONString(request));
            CommonResponse response = client.getCommonResponse(request);
            log.debug("短信发送结果: {}", JSON.toJSONString(response));
            return response;
        } catch (ServerException e) {
            log.error("短信发送异常(ServerException):", e);
        } catch (ClientException e) {
            log.error("短信发送异常(ClientException):", e);
        }
        return null;
    }

    private IAcsClient getAcsClinet() {
        DefaultProfile profile = DefaultProfile.getProfile(aliyunSmsProperties.getRegionId(), aliyunSmsProperties.getAccessKey(), aliyunSmsProperties.getAccessSecret());
        return new DefaultAcsClient(profile);
    }

    private CommonRequest getCommonRequest() {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(aliyunSmsProperties.getDomain());
        request.setVersion("2017-05-25");
        request.setVersion(DateUtil.today());
        return request;
    }
}
