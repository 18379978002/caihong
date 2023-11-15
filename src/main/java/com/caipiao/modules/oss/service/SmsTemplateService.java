package com.caipiao.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.modules.oss.entity.SmsTemplate;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/10 20:22
 */
public interface SmsTemplateService extends IService<SmsTemplate> {

    void addTemplate(SmsTemplate template);

    void updateTemplate(SmsTemplate template, String companyId);

    void updateTemplateStatus(String templateCode, String companyId);

    void deleteTemplate(String templateCode);

    PageUtils queryPage(Integer page, Integer limit, String templateName, Integer templateStatus, String companyId);
}
