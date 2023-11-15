package com.caipiao.modules.sys.form;

import lombok.Data;

@Data
public class LinkInvitationReqDTO {
    private String userId;
    private String invitationCode;
}
