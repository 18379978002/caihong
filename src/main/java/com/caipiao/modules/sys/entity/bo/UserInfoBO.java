package com.caipiao.modules.sys.entity.bo;

import com.caipiao.modules.order.entity.enmu.UserType;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.form.UserInfoDTO;
import lombok.Data;


@Data
public class UserInfoBO {
    private String phoneNumber;
    private String password;
    //昵称
    private String nickName;
    //邀请码
    private String invitationCode;

    private String avatar;
    private String sex;

    private String realName;
    private String idCard;
    private String manageStaffId;
    private UserType userType;

    private Integer subordinateStore;


    public static UserInfoBO coverUserInfoBO(UserInfoDTO userInfoDTO){
        UserInfoBO userInfoBO = new UserInfoBO();
        userInfoBO.setPhoneNumber(userInfoDTO.getPhoneNumber());
        userInfoBO.setPassword(userInfoDTO.getPassword());
        userInfoBO.setNickName(userInfoDTO.getNickName());
        userInfoBO.setInvitationCode(userInfoDTO.getInvitationCode());
        userInfoBO.setAvatar(userInfoDTO.getAvatar());
        userInfoBO.setSex(userInfoDTO.getSex());
        userInfoBO.setRealName(userInfoDTO.getRealName());
        userInfoBO.setIdCard(userInfoDTO.getIdCard());
        userInfoBO.setManageStaffId(userInfoDTO.getManageStaffId());
        userInfoBO.setUserType(UserType.USER);
        return userInfoBO;
    }

    public static UserInfoBO coverUserInfoBO(SysCompStaffEntity staff) {
        UserInfoBO userInfoBO = new UserInfoBO();
        userInfoBO.setPassword(staff.getStaffPasswd());
        userInfoBO.setNickName(staff.getStaffId());
        userInfoBO.setPhoneNumber(staff.getMobile());
//        userInfoBO.setInvitationCode();
        userInfoBO.setAvatar(staff.getAvatar());
        userInfoBO.setRealName(staff.getStaffName());
        userInfoBO.setManageStaffId(staff.getStaffId());
        userInfoBO.setUserType(UserType.AGENT);
        return userInfoBO;
    }
}
