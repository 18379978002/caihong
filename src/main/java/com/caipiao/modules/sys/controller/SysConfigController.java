package com.caipiao.modules.sys.controller;


import com.caipiao.common.annotation.SysLog;
import com.caipiao.modules.sys.entity.SysConfigEntity;
import com.caipiao.modules.sys.service.SysConfigService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置信息
 *
 */
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 所有配置列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:config:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("companyId", getCompanyId());
        PageUtils page = sysConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 配置信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:config:info")
    public R info(@PathVariable("id") Long id) {
        SysConfigEntity config = sysConfigService.getById(id);
        if(!config.getCompanyId().equals(getCompanyId())){
            return error("暂无权限");
        }
        return R.ok().put("config", config);
    }

    /**
     * 保存配置
     */
    @SysLog("保存配置")
    @PostMapping("/save")
    @RequiresPermissions("sys:config:save")
    public R save(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);
        config.setCompanyId(getCompanyId());
        sysConfigService.saveConfig(config);
        return R.ok();
    }

    /**
     * 修改配置
     */
    @SysLog("修改配置")
    @PostMapping("/update")
    @RequiresPermissions("sys:config:update")
    public R update(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);
        config.setCompanyId(getCompanyId());
        sysConfigService.update(config);
        return R.ok();
    }

    /**
     * 删除配置
     */
    @SysLog("删除配置")
    @PostMapping("/delete")
    @RequiresPermissions("sys:config:delete")
    public R delete(@RequestBody Long[] ids) {

        sysConfigService.deleteBatch(ids, getCompanyId());
        return R.ok();
    }

}
