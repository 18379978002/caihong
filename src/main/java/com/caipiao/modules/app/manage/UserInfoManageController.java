package com.caipiao.modules.app.manage;

import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.dto.UserInfoReq;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.sys.controller.AbstractController;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午2:21
 */
@RestController
@RequestMapping("/manage/userinfo")
@Api(tags = "app用户管理")
@Slf4j
@RequiredArgsConstructor
public class UserInfoManageController extends AbstractController {

    private final UserInfoService userInfoService;

    @ApiOperation("app用户列表")
    @GetMapping("list")
    public R list(UserInfoReq req){

        if(!isShopManager()){
            req.setStaffId(getUserId());
        }
        //手动加店铺id
        SysCompStaffEntity staff = getUser();
        req.setShop(staff.getSubordinateStore());
        return userInfoService.queryPage(req);
    }

    @ApiOperation("app用户详情")
    @GetMapping("{id}")
    public R get(@PathVariable String id){
        UserInfo userInfo = userInfoService.getById(id);
        return ok().put(userInfo);
    }

    @ApiOperation("拉黑")
    @PostMapping("/delete/{id}")
    public R del(@PathVariable String id){
        UserInfo userInfo = userInfoService.getById(id);
        userInfo.setDelFlag(Constant.YES);
        userInfoService.updateById(userInfo);
        return ok();
    }

    @ApiOperation("给用户充值")
    @PostMapping("recharge")
    public R recharge(@RequestParam("userId")String userId,
                      @RequestParam("amount")BigDecimal amount){
        userInfoService.recharge(userId, amount);
        return ok();
    }

    @ApiOperation("给用户减款")
    @PostMapping("subtract")
    public R subtract(@RequestParam("userId")String userId,
                      @RequestParam("amount")BigDecimal amount){
        userInfoService.subtract(userId, amount);
        return ok();
    }
}
