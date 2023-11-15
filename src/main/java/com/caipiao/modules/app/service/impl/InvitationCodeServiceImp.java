package com.caipiao.modules.app.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.InvitationCodeService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.order.entity.enmu.UserType;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.service.ISysCompStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 邀请码业务实现
 */
@Service
public class InvitationCodeServiceImp implements InvitationCodeService {

    @Resource
    private UserInfoService userInfoService;

    @Autowired
    private ISysCompStaffService sysCompStaffService;       
    /**
     * 查询上级代理，在此刷新用户表邀请码
     * @param userId
     * @param invitationCode
     */
    //给用户设置ManageStaffId
    @Override
    public void linkAndfreshInvitaAndtionCode(String userId, String invitationCode) {
        UserInfo userInfo = userInfoService.getById(userId);
        if (StringUtils.isNotBlank(userInfo.getManageStaffId())){
            throw new RRException("已绑定代理,无需重复绑定");
        }
        UserInfo agentUserInfo = linkAndfreshInvitaAndtionCode(invitationCode);
        SysCompStaffEntity agentStaff = sysCompStaffService.getByUserId(agentUserInfo.getId());
        if (userInfo.getInvitationCode()==null){
            String userInfoInvitationCode = getInvitationCode();
            userInfo.setInvitationCode(userInfoInvitationCode);
        }

        userInfo.setManageStaffId(agentStaff.getStaffId());
        userInfo.setParentInvitationCode(invitationCode);
        userInfoService.updateById(userInfo);
    }

    //通过传入的邀请码递归出顶层代理人
    public UserInfo linkAndfreshInvitaAndtionCode(String invitationCode){
        UserInfo parentUserInfo = userInfoService.getUserByInvitationCode(invitationCode);
        if (null == parentUserInfo) {
            throw new RRException("错误的邀请码!");
        }
        if (UserType.USER == parentUserInfo.getUserType()) {
            return linkAndfreshInvitaAndtionCode(parentUserInfo.getParentInvitationCode());
        }
        return parentUserInfo;
    }
    /**
     * 这块做递归
     * @param userId
     * @param invitationCode
     * @param userInfo
     *//*
    private void linkAndfreshInvitaAndtionCode(String userId, String invitationCode, UserInfo userInfo) {
        UserInfo parentUserInfo = userInfoService.getUserByInvitationCode(invitationCode);
        if (null == parentUserInfo) {
            throw new RRException("错误的邀请码!");
        }
        if (UserType.USER == parentUserInfo.getUserType()) {
            linkAndfreshInvitaAndtionCode(userId, userInfo.getInvitationCode(),parentUserInfo);
            return;
        }
        dolinkAndFreshInvitandtionCode(userId,invitationCode,parentUserInfo.getManageStaffId());
    }


    *//**
     * 操作数据库
     * @param userId
     * @param invitationCode
     * @param parentManagerId
     *//*
    private void dolinkAndFreshInvitandtionCode(String userId,String invitationCode,String parentManagerId) {
        SysCompStaffEntity agentInfo = sysCompStaffService.getByUserId(parentManagerId);
        UserInfo userInfo = userInfoService.getById(userId);
        userInfo.setManageStaffId(agentInfo.getStaffId());
        userInfo.setInvitationCode(getInvitationCode());
        userInfo.setParentInvitationCode(invitationCode);
        userInfoService.updateById(userInfo);
    }*/


    /**
     * 获取唯一邀请码
     * 这里直接用UserDao操作
     * @return
     */
    public String getInvitationCode(){

        String code = RandomUtil.randomString(Constant.BASE_STR, 8);

        UserInfo userByInvitationCode = userInfoService.getUserByInvitationCode(code);
        if (userByInvitationCode!=null) {
            return getInvitationCode();
        }
        return code;
    }

}
