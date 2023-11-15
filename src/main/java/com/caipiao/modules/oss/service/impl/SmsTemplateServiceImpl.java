package com.caipiao.modules.oss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.DateUtils;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.oss.dao.SmsTemplateDao;
import com.caipiao.modules.oss.entity.SmsTemplate;
import com.caipiao.modules.oss.service.IAliyunSmsService;
import com.caipiao.modules.oss.service.SmsTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/10 20:22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateDao, SmsTemplate> implements SmsTemplateService {
    private final IAliyunSmsService aliyunSmsService;

    @Override
    public void addTemplate(SmsTemplate template) {

        CommonResponse commonResponse = aliyunSmsService.addSmsTemplate(template.getTemplateType() + "",
                template.getTemplateName(), template.getTemplateContent(), template.getRemark());

        JSONObject object = JSONObject.parseObject(commonResponse.getData());

        String code = object.getString("Code");

        if(code.equalsIgnoreCase("OK")){
            //成功添加
            String templateCode = object.getString("TemplateCode");

            template.setTemplateCode(templateCode);
            template.setCreateDate(new Date());
            template.setMessage(object.getString("Message"));
            template.setTemplateStatus(0);//默认审核中

            this.save(template);
        }else{
            throw new RRException(object.getString("Message"));
        }

    }

    @Override
    public void updateTemplate(SmsTemplate template, String companyId) {

        SmsTemplate t1 = this.getById(template);

        if(null == t1 || t1.getTemplateStatus()!=2 || !companyId.equals(t1.getCompanyId())){
            throw new RRException("当前状态不可修改");
        }

        CommonResponse commonResponse = aliyunSmsService.modifySmsTemplate(template.getTemplateType()+"", template.getTemplateName(), template.getTemplateContent(), template.getTemplateCode(), template.getRemark());

        JSONObject object = JSONObject.parseObject(commonResponse.getData());

        String code = object.getString("Code");

        if(code.equalsIgnoreCase("OK")){
            //成功添加
            template.setCompanyId(companyId);
            template.setTemplateStatus(0);//默认审核中
            this.updateById(template);
        }else{
            throw new RRException(object.getString("Message"));
        }

    }

    @Override
    public void updateTemplateStatus(String templateCode, String companyId) {

        SmsTemplate byId = this.getById(templateCode);
        if(null == byId || byId.getTemplateStatus()!=2 || !companyId.equals(byId.getCompanyId())){
            throw new RRException("当前状态不可修改");
        }

        CommonResponse commonResponse = aliyunSmsService.querySmsTemplate(templateCode);

        JSONObject object = JSONObject.parseObject(commonResponse.getData());

        String code = object.getString("Code");
        if(code.equalsIgnoreCase("OK")){

            SmsTemplate template = new SmsTemplate();
            template.setCreateDate(DateUtils.str2Date(object.getString("CreateDate"), DateUtils.DATE_TIME_PATTERN));
            template.setTemplateStatus(object.getInteger("TemplateStatus"));
            template.setMessage(object.getString("Message"));
            template.setTemplateCode(object.getString("TemplateCode"));
            template.setReason(object.getString("Reason"));
            template.setTemplateContent(object.getString("TemplateContent"));
            template.setTemplateName(object.getString("TemplateName"));
            template.setTemplateType(object.getInteger("TemplateType"));
            template.setCompanyId(companyId);
            this.updateById(template);
        }else{
            throw new RRException(object.getString("Message"));
        }


    }

    @Override
    public void deleteTemplate(String templateCode) {
        SmsTemplate template = this.getById(templateCode);
        if(template.getTemplateStatus()==0){
            throw new RRException("当前状态不可删除");
        }

        CommonResponse commonResponse = aliyunSmsService.deleteSmsTemplate(templateCode);

        JSONObject object = JSONObject.parseObject(commonResponse.getData());

        String code = object.getString("Code");

        if(code.equalsIgnoreCase("OK")){
            //成功删除
            this.removeById(templateCode);
        }else{
            throw new RRException(object.getString("Message"));
        }
    }

    @Override
    public PageUtils queryPage(Integer page, Integer limit, String templateName, Integer templateStatus, String companyId) {
        IPage<SmsTemplate> pg = new Page<>(page, limit);
        LambdaQueryWrapper<SmsTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(templateName), SmsTemplate::getTemplateName, templateName)
                    .eq(SmsTemplate::getCompanyId, companyId)
                    .eq(null != templateStatus, SmsTemplate::getTemplateStatus, templateStatus)
                    .orderByDesc(SmsTemplate::getCreateDate);
        IPage<SmsTemplate> page1 = this.page(pg, wrapper);
        return new PageUtils(page1);
    }
}
