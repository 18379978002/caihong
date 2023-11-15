package com.caipiao.modules.app.controller;

import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.InvitationCodeService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.order.dao.OrderDao;
import com.caipiao.modules.order.dto.MyDataDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/app/mydata")
@Slf4j
@Api(tags = "《app端》我的页面数据管理")
public class MyDataController extends AppAbstractController{

    @Resource
    private OrderDao orderDao;

    @Resource
    private UserInfoService userInfoService;

    @Autowired
    private InvitationCodeService invitationCodeService;

    @ApiOperation("获取我的数据")
    @GetMapping("getmydata")
    public R getMyData(){
        String id = getUser().getId();
        UserInfo userInfo = userInfoService.getById(id);
        if (StringUtils.isBlank(userInfo.getNickName())) {
            String invitationCode = invitationCodeService.getInvitationCode();
            userInfo.setNickName(invitationCode);
            userInfoService.updateById(userInfo);
        }

        MyDataDTO myDataDTO = orderDao.queryStatusNumCount(id);
        myDataDTO.setTodayBonus(userInfo.getTodayBonus());
        myDataDTO.setBalance(userInfo.getBalance());
        myDataDTO.setUserTag(userInfo.getUserType().toString());
        myDataDTO.setHasSuperiorManageId(null == userInfo.getManageStaffId());
        myDataDTO.setNickName(userInfo.getNickName());

        if (StringUtils.isBlank(userInfo.getHeadimgUrl())) {
            myDataDTO.setAvatar(Constant.USER_DEFAULT_AVATAR);
        }else {
            myDataDTO.setAvatar(userInfo.getHeadimgUrl());
        }

        return ok().put(myDataDTO);

    }

}
