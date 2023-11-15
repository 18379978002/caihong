package com.caipiao.modules.app.manage;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.common.utils.*;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.modules.app.dto.WithdrawReq;
import com.caipiao.modules.app.entity.UserWithdrawRecord;
import com.caipiao.modules.app.service.UserWithdrawRecordService;
import com.caipiao.modules.sys.controller.AbstractController;
import com.caipiao.modules.sys.service.StaffManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:58
 */
@RestController
@RequestMapping("/manage/userwithdrawrecord")
@Api(tags = "用户提现管理")
@Slf4j
@RequiredArgsConstructor
public class UserWithdrawRecordManageController extends AbstractController {

    private final UserWithdrawRecordService userWithdrawRecordService;

    private final StaffManageService staffManageService;

    @PostMapping("verify")
    @ApiOperation("审核")
    public R verify(@RequestBody WithdrawReq req){
        ValidatorUtils.validateEntity(req);

        if(!req.getResult().equals("1") && !req.getResult().equals("2")){
            return error("审核结果必须为1审核通过，或者 2审核不通过");
        }
        userWithdrawRecordService.verify(req, getUser());
        return ok();
    }

    @GetMapping("list")
    @ApiOperation("查询提现记录")
    public R apply(@RequestParam Map<String, Object> params){
        IPage<UserWithdrawRecord> page = new Query<UserWithdrawRecord>().getPage(params);

        List<String> userIds = new ArrayList<>();

        if(!isShopManager()){
            userIds = queryStaffManageList();
            if(CollUtil.isEmpty(userIds)){
                return ok();
            }
        }



        String realName = (String)params.get("realName");
        String status = (String)params.get("status");

        if(null== status || status.equals("null")){
            status = "";
        }

        LambdaQueryWrapper<UserWithdrawRecord> wrapper = new LambdaQueryWrapper<>();
        //传3代表历史记录
        if(status.equals("3")){
            wrapper.like(StringUtils.isNotBlank(realName), UserWithdrawRecord::getRealName, realName)
                    .and(wrapper11->wrapper11.eq(UserWithdrawRecord::getStatus, "2").or().eq(UserWithdrawRecord::getStatus, "1"))
                    .eq(UserWithdrawRecord::getDelFlag, Constant.NO);
        }else{
            wrapper.like(StringUtils.isNotBlank(realName), UserWithdrawRecord::getRealName, realName)
                    .eq(StringUtils.isNotBlank(status), UserWithdrawRecord::getStatus, status)
                    .eq(UserWithdrawRecord::getDelFlag, Constant.NO);
        }

        if(CollUtil.isNotEmpty(userIds)){
            wrapper.in(UserWithdrawRecord::getUserId, userIds);
        }


        IPage<UserWithdrawRecord> page1 = userWithdrawRecordService.page(page, wrapper);

        return ok().put("page", new PageUtils<>(page1));
    }




}
