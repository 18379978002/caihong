package com.caipiao.modules.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午2:31
 */
@Data
public class UserInfoReq implements Serializable {
    private Integer page = 1;
    private Integer limit = 10;
    private String sidx;
    private String order;
    private String nickName;
    private String phone;
    private String realName;
    private String staffId;

    private String manageStaffId;

    //手动加店铺id需要
    private String shop;
}
