package com.caipiao.modules.oss.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.oss.entity.SmsSendLog;
import com.caipiao.modules.oss.entity.req.SendSmsDTO;
import com.caipiao.modules.oss.service.SmsSendLogService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/12 20:34
 */
@RestController
@RequestMapping("manage/send_log")
@Api(tags = "发送记录")
public class SmsSendController extends AbstractController {

    @Autowired
    private SmsSendLogService smsSendLogService;

    @PostMapping("batchSend")
    @ApiOperation("批量发送")
    @RequiresPermissions("sms:batch:send")
    public R send(@RequestBody SendSmsDTO dto){
        smsSendLogService.batchSend(dto, getCompanyId());
        return R.ok();
    }

    @GetMapping("list")
    @ApiOperation("分页查询")
    public R list(@RequestParam(value = "page", required = false, defaultValue = "1")Integer page,
                  @RequestParam(value = "limit", required = false, defaultValue = "10")Integer limit,
                  @RequestParam(value = "phoneNumber", required = false)String phoneNumber){
        IPage<SmsSendLog> pg = new Page<>(page, limit);
        LambdaQueryWrapper<SmsSendLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(phoneNumber), SmsSendLog::getPhoneNumber, phoneNumber)
                    .eq(SmsSendLog::getCompanyId, getCompanyId());
        IPage<SmsSendLog> page1 = smsSendLogService.page(pg, wrapper);
        return R.ok().put("page", page1);
    }


}
