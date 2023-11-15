package com.caipiao.modules.app.manage;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.entity.UserWithdrawRecord;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.app.service.UserWithdrawRecordService;
import com.caipiao.modules.order.dao.OrderDao;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.sys.controller.AbstractController;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.service.ISysCompStaffService;
import com.caipiao.modules.sys.service.StaffManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/mydata")
@Slf4j
@Api(tags = "《管理端》我的页面数据管理")
public class MyDataManageController extends AbstractController {
    @Autowired
    OrderDao orderDao;
    @Autowired
    UserWithdrawRecordService userWithdrawRecordService;
    @Autowired
    StaffManageService staffManageService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    private ISysCompStaffService sysCompStaffService;

    @ApiOperation("获取我的数据")
    @GetMapping("getmydata")
    public R getMyData(){
        SysCompStaffEntity staff = getUser();

        List<String> userIds = new ArrayList<>();

        if(!isShopManager()){
            userIds = queryStaffManageList();
            if(CollUtil.isEmpty(userIds)){
                Map<String, Integer> mp = new HashMap<>();
                mp.put("waitTicketNum",0);
                mp.put("waitWithDrawNum",0);

                return ok().put(mp);
            }
        }

        //查询等待出票数量
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getPlanStatus,1);

        if(CollUtil.isNotEmpty(userIds)){
            wrapper.in(Order::getUserId, userIds);
        }
        Long waitTicketNum = orderDao.selectCount(wrapper);

        //查询待提现数量
        LambdaQueryWrapper<UserWithdrawRecord> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(UserWithdrawRecord::getStatus,0);

        if(CollUtil.isNotEmpty(userIds)){
            wrapper1.in(UserWithdrawRecord::getUserId, userIds);
        }

        Long waitWithDrawNum = userWithdrawRecordService.count(wrapper1);

        Map<String, String> mp = new HashMap<>();
        mp.put("waitTicketNum",String.valueOf(waitTicketNum.intValue()));
        mp.put("waitWithDrawNum",String.valueOf(waitWithDrawNum.intValue()));

        if (StringUtils.isBlank(staff.getAvatar())) {
            mp.put("filepath",Constant.STAFF_DEFAULT_AVATAR);
        }else {
            mp.put("filepath",staff.getAvatar());
        }
        return ok().put(mp);

    }

    /**
     * 获取店主邀请码
     * @return
     */
    @ApiOperation("获取店主邀请码")
    @GetMapping("invitationCode")
    public R InvitationCode(){
        SysCompStaffEntity staff = getUser();
        UserInfo userInfo = userInfoService.getById(staff.getUserId());
        if (userInfo==null) {
            return R.error("店主未初始化用户账号");
        }
        return ok().put(userInfo.getInvitationCode());
    }

}
