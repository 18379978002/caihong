package com.caipiao.modules.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.dto.UserInfoReq;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.sys.entity.bo.UserInfoBO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:21
 */
public interface UserInfoService extends IService<UserInfo> {
    R queryPage(UserInfoReq req);


    IPage<UserInfo> queryStaffManage(Map<String, Object> params);

    void recharge(String userId, BigDecimal amount);

    void subtract(String userId, BigDecimal amount);

    UserInfo registerUserInfo(UserInfoBO userInfoBO);

    public UserInfo getUserByInvitationCode(String invitationCode);

}
