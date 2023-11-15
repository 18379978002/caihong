package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午7:29
 */
@Data
@ApiModel("作者申请表")
public class AuthorApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * id
     */
    @NotBlank(message = "ID不能为空", groups = UpdateGroup.class)
    @ApiModelProperty("id")
    private String id;

    /**
     * 作者姓名
     */
    @NotBlank(message = "作者姓名不能为空", groups = AddGroup.class)
    @ApiModelProperty("作者姓名")
    private String staffName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createDate;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空", groups = AddGroup.class)
    @ApiModelProperty("密码")
    private String staffPasswd;

    /**
     * 重复密码
     */
    @TableField(exist = false)
    @NotBlank(message = "重复密码不能为空", groups = AddGroup.class)
    @ApiModelProperty("重复密码")
    private String rePasswd;

    /**
     * 工号状态0，审核中 1、审核通过
     */
    @ApiModelProperty("工号状态0，审核中 1、审核通过 2、审核被拒绝")
    private String staffStatus;

    /**
     * 登录名称
     */
    @NotBlank(message = "登录名称不能为空", groups = AddGroup.class)
    @ApiModelProperty("登录名称")
    private String loginName;

    /**
     * 邀请码
     */
    @ApiModelProperty("邀请码")
    @NotBlank(message = "邀请码不能为空", groups = AddGroup.class)
    private String invitationCode;

    @ApiModelProperty("备注，驳回理由等")
    private String remark;

    public AuthorApply() {}
}
