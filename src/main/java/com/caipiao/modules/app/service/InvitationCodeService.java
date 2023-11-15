package com.caipiao.modules.app.service;

import com.caipiao.modules.app.entity.UserInfo;

public interface InvitationCodeService {

    //给用户设置ManageStaffId
    public void linkAndfreshInvitaAndtionCode(String userId, String invitationCode);

    //通过邀请码推出顶层代理人
    public UserInfo linkAndfreshInvitaAndtionCode(String invitationCode);

    public String getInvitationCode();
}
