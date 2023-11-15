package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caipiao.common.validator.group.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午7:37
 */
@Data
@ApiModel("邀请码")
public class InvitationCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * id
     */
    @ApiModelProperty("id")
    private String id;

    /**
     * 邀请码
     */
    @NotBlank(message = "邀请码不能为空", groups = AddGroup.class)
    @ApiModelProperty("邀请码")
    private String invitationCode;

    /**
     * 状态 0未使用 1已使用
     */
    @TableField(value = "`status`")
    @ApiModelProperty("状态 0未使用 1已使用")
    private int status;

    /**
     * create_time
     */
    @ApiModelProperty("create_time")
    private Date createTime;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String createId;

    /**
     * 使用id
     */
    @ApiModelProperty("使用id")
    private String usedId;

    /**
     * 使用时间
     */
    @ApiModelProperty("使用时间")
    private Date usedTime;

    public InvitationCode() {}
}
