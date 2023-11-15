package com.caipiao.modules.oss.controller;

import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import com.caipiao.modules.oss.entity.SmsTemplate;
import com.caipiao.modules.oss.service.SmsTemplateService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/10 20:51
 */
@RestController
@RequestMapping("manage/sms_template")
@Api(tags = "短信模板管理")
public class SmsTemplateController extends AbstractController {
    @Autowired
    SmsTemplateService smsTemplateService;

    @PostMapping
    @ApiOperation("新增模板")
    public R save(@RequestBody SmsTemplate template){
        ValidatorUtils.validateEntity(template, AddGroup.class);
        template.setCompanyId(getCompanyId());
        smsTemplateService.addTemplate(template);
        return ok();
    }

    @PutMapping
    @ApiOperation("修改模板")
    public R update(@RequestBody SmsTemplate template){
        ValidatorUtils.validateEntity(template, UpdateGroup.class);
        smsTemplateService.updateTemplate(template, getCompanyId());
        return ok();
    }

    @DeleteMapping("{templateCode}")
    @ApiOperation("删除模板")
    public R delete(@PathVariable String templateCode){
        SmsTemplate byId = smsTemplateService.getById(templateCode);
        if(null == byId || !getCompanyId().equals(byId.getCompanyId())){
            return error("暂无权限");
        }

        smsTemplateService.deleteTemplate(templateCode);
        return ok();
    }

    @GetMapping("{templateCode}")
    @ApiOperation("查询模板")
    public R get(@PathVariable String templateCode){
        SmsTemplate byId = smsTemplateService.getById(templateCode);
        if(null == byId || !getCompanyId().equals(byId.getCompanyId())){
            return error("暂无权限");
        }
        return ok().put(byId);
    }

    @GetMapping("list")
    @ApiOperation("分页查询")
    public R queryPage(@RequestParam(value = "page", required = false, defaultValue = "1")Integer page,
                       @RequestParam(value = "limit", required = false, defaultValue = "10")Integer limit,
                       @RequestParam(value = "templateName", required = false)String templateName,
                       @RequestParam(value = "templateStatus", required = false)Integer templateStatus){
        return ok().put("page", smsTemplateService.queryPage(page, limit, templateName, templateStatus, getCompanyId()));
    }

    @PutMapping("updateStatus/{templateCode}")
    @ApiOperation("更新状态")
    public R updateStatus(@PathVariable String templateCode){
        smsTemplateService.updateTemplateStatus(templateCode, getCompanyId());
        return ok();
    }


}
