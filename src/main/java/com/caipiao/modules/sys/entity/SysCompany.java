package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/20 10:15
 */
@Data
@TableName("sys_company")
public class SysCompany implements Serializable {
    @TableId(type = IdType.INPUT)
    @NotBlank(message = "公司id不可为空", groups = UpdateGroup.class)
    private String id;
    @NotBlank(message = "公司名称不可为空", groups = AddGroup.class)
    private String companyName;
    private String companyCode;
    @JsonIgnore
    private String appId;
    @JsonIgnore
    private String appSecret;
    private Date updateTime;
    /** 1正常 0禁止 **/
    private Integer companyStatus;
    @NotBlank(message = "corpId不可为空", groups = AddGroup.class)
    private String corpId;
    @TableField(exist = false)
    private CompanyAppConfig config;
    @TableField(exist = false)
    @NotNull(message = "agentId不可为空", groups = AddGroup.class)
    private Long agentId;
    @TableField(exist = false)
    @NotBlank(message = "secret不可为空", groups = AddGroup.class)
    private String secret;
    @NotBlank(message = "管理员账号不可为空", groups = AddGroup.class)
    private String superAdminAccount;
    @NotBlank(message = "管理员密码不可为空", groups = AddGroup.class)
    private String superAdminPassword;

    private String kfId;
    private String kfName;
    private String remark;

    @TableField(exist = false)
    private List<Long> superAdminMenus;

    @TableField(exist = false)
    private List<CompanyApp> apps;


}
