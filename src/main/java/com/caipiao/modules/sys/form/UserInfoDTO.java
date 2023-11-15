package com.caipiao.modules.sys.form;

import com.caipiao.common.validator.annotation.MobileValidator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 上午9:24
 */
@Data
public class UserInfoDTO implements Serializable {
    @MobileValidator
    private String phoneNumber;
    @NotBlank(message = "验证码不能为空")
    private String code;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "重复不能为空")
    private String rePassword;
    //昵称
    private String nickName;
    //邀请码
    private String invitationCode;

    private String avatar;
    private String sex;

    private String realName;
    private String idCard;
//    @NotBlank(message = "分享人不能为空")
    private String manageStaffId;
}
