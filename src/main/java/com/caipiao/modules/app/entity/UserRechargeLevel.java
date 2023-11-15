package com.caipiao.modules.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午4:01
 */
@Data
@ApiModel("充值档位")
public class UserRechargeLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * id
     */
    @ApiModelProperty("id")
    @NotBlank(message = "ID不能为空", groups = UpdateGroup.class)
    private String id;

    /**
     * 充值金额
     */
    @ApiModelProperty("充值金额")
    @NotNull(message = "充值金额不能为空", groups = AddGroup.class)
    private BigDecimal rechargeAmount;

    /**
     * 充值奖励游戏币
     */
    @NotNull(message = "充值奖励游戏币不能为空", groups = AddGroup.class)
    @ApiModelProperty("充值奖励游戏币")
    private BigDecimal bonusAmount;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    @TableField("`sort`")
    private Integer sort;

    /**
     * 充值描述
     */
    @ApiModelProperty("充值描述")
    @TableField("`desc`")
    private String desc;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    public UserRechargeLevel() {}
}
