package com.caipiao.modules.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.common.utils.*;
import com.caipiao.modules.app.controller.AppAbstractController;
import com.caipiao.modules.app.entity.Fans;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.FansService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.common.util.HeatUtil;
import com.caipiao.modules.order.dao.OrderDao;
import com.caipiao.modules.order.dto.AddBillingDTO;
import com.caipiao.modules.order.dto.UserBriefInfo;
import com.caipiao.modules.order.entity.Billing;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.order.service.BillingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/billing")
@Api(tags = "《app端》发单跟单管理")
@Slf4j
@RequiredArgsConstructor
public class BillingController extends AppAbstractController {
    private final BillingService billingService;
    private final UserInfoService userInfoService;
    private final FansService fansService;
    private final OrderDao orderDao;

    @ApiOperation("跟单")
    @PostMapping("addBilling")
    public R addBilling(@RequestBody AddBillingDTO dto){
        if(dto.getBillingId() == null){
            return error("参数不正确");
        }
        if(dto.getMultiple() < 1){
            return error("投注倍数最低为1");
        }
        Long aLong = billingService.addBilling(dto, getUser());
        return ok().put(aLong);
    }

    @GetMapping("queryDetail/{billingId}")
    @ApiOperation("查询跟单")
    public R queryDetail(@PathVariable Long billingId){
        Billing billing = billingService.queryDetail(billingId, getUser());
        return ok().put(billing);
    }

    @GetMapping("list")
    @ApiOperation("分页查询跟单")
    public R list(@RequestParam Map<String, Object> params){

        //排序放到这里，可以按照方案金额，amount和热度billingHeat，进行排序
        IPage<Billing> page = new Query<Billing>().getPage(params);
        String userId = (String) params.get("userId");
        LambdaQueryWrapper<Billing> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(userId), Billing::getUserId, userId)
                .in(Billing::getPlanStatus, 1, 2)
                .apply("DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time)");
        IPage<Billing> iPage = billingService.page(page, wrapper);

        //热度计算
        List<Billing> records = iPage.getRecords();
        for (Billing record : records) {
            record.setBillingHeat(HeatUtil.getHeat(record.getBillingHeat()));
        }

        iPage.setRecords(records);

        return ok().put("page", new PageUtils<>(iPage));
    }

    @GetMapping("queryPlanGod")
    @ApiOperation("查询方案大神")
    public R queryPlanGod(){

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        //按照带红人次降序
        wrapper.select(UserInfo::getNickName, UserInfo::getHeadimgUrl, UserInfo::getId)
                //只有带红人次大于1才有资格成为方案大神
                .ge(UserInfo::getRedFansNum, 1)
                .orderByDesc(UserInfo::getRedFansNum).last("limit 8");

        List<UserInfo> list = userInfoService.list(wrapper);

        return ok().put(list);
    }

    @PostMapping("attention/{userId}")
    @ApiOperation("关注和取消关注")
    public R attention(@PathVariable String userId){
        fansService.attention(userId, getUser());
        return ok();
    }

    @GetMapping("getUserBriefInfo/{userId}")
    @ApiOperation("获取粉丝关注等基础用户信息")
    public R getUserBriefInfo(@PathVariable String userId){
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(UserInfo::getNickName,
                        UserInfo::getHeadimgUrl,
                        UserInfo::getRedFansNum,
                        UserInfo::getAttentionNum,
                        UserInfo::getFansNum)
                .eq(UserInfo::getId, userId);
        UserInfo userInfo = userInfoService.getOne(wrapper);

        UserBriefInfo info = new UserBriefInfo();
        BeanUtils.copyProperties(userInfo, info);

        info.setUserId(userId);
        info.setSelf(userId.equals(getUserId()));

        LambdaQueryWrapper<Fans> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Fans::getFansId, getUserId())
                .eq(Fans::getUserId, userId);

        Fans one = fansService.getOne(wrapper1);

        info.setAttention(null != one);

        return ok().put(info);
    }

    @GetMapping("last7day/{userId}")
    @ApiOperation("近七日数据")
    public R last7day(@PathVariable String userId){

        QueryWrapper<Order> wrapper = new QueryWrapper<>();

        wrapper.select(
                "sum(bonus_amount) as bonusAmount",
                "count(if(plan_status in(1,2),1,null)) as orderNum",
                "count(if(win_status in(3,4), 1, null)) as redNum"
        ).eq("user_id", userId)
                .eq("is_billing", 1)
                .apply("DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time)");

        List<Map<String, Object>> maps = orderDao.selectMaps(wrapper);
        return ok().put(maps);
    }




}
