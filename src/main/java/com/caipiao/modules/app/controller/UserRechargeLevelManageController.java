package com.caipiao.modules.app.controller;

import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import com.caipiao.modules.app.entity.UserRechargeLevel;
import com.caipiao.modules.app.service.UserRechargeLevelService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午4:05
 */
@RestController
@RequestMapping("manage/userrechargelevel")
@Api(tags = "充值等级管理《后台》")
@RequiredArgsConstructor
public class UserRechargeLevelManageController extends AbstractController {
    private final UserRechargeLevelService userRechargeLevelService;


    @ApiOperation("新增")
    @PostMapping
    public R save(@RequestBody UserRechargeLevel userRechargeLevel){
        ValidatorUtils.validateEntity(userRechargeLevel, AddGroup.class);
        userRechargeLevel.setCreateTime(new Date());
        userRechargeLevelService.save(userRechargeLevel);
        return ok();
    }

    @ApiOperation("修改")
    @PutMapping
    public R update(@RequestBody UserRechargeLevel gradeInfo){
        ValidatorUtils.validateEntity(gradeInfo, UpdateGroup.class);
        userRechargeLevelService.updateById(gradeInfo);
        return ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("{id}")
    public R del(@PathVariable Long id){

       userRechargeLevelService.removeById(id);
        return ok();
    }

    @ApiOperation("查询")
    @GetMapping("{id}")
    public R get(@PathVariable Long id){
        UserRechargeLevel info = userRechargeLevelService.getById(id);
        return ok().put(info);
    }

    @ApiOperation("分页查询")
    @GetMapping("list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils pageUtils = userRechargeLevelService.queryPage(params);
        return ok().put("page", pageUtils);
    }
}
