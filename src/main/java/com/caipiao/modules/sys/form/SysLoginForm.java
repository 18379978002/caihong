package com.caipiao.modules.sys.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录表单
 *
 */
@Data
public class SysLoginForm {
    @ApiModelProperty(value = "登录用户名",required = true)
    private String username;
    @ApiModelProperty(value = "登录密码",required = true)
    private String password;
    @ApiModelProperty(value = "验证码图片",notes = "可使用验证码接口获取",required = true)
    private String captcha;
    @ApiModelProperty(value = "验证码图片对应UUID",notes = "获取验证码时填写的那个",required = true)
    private String uuid;
    private String newPassword;

    private String phoneNumber;
}
