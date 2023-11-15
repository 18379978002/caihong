package com.caipiao.modules.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @date 2022/2/21 下午3:46
 */
@Data
@ApiModel("用户提现记录")
public class UserWithdrawRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * pk
     */
    @ApiModelProperty("pk")
    private String id;

    /**
     * 逻辑删除标记（0：显示；1：隐藏）
     */
    @ApiModelProperty("逻辑删除标记（0：显示；1：隐藏）")
    private String delFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    private Date updateTime;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String userId;

    /**
     * 提现类型（1：分销拥金提现 2、作者收入提现）
     */
    @ApiModelProperty("提现类型（1：分销拥金提现 2、作者收入提现）")
    private String withdrawType;

    /**
     * 申请金额
     */
    @ApiModelProperty("申请金额")
    @NotNull(message = "提现金额不能为空")
    private BigDecimal applyAmount;

    /**
     * 收款方式（1：银行卡）
     */
    @ApiModelProperty("收款方式（1：银行卡）")
    private String paymentMethod;

    /**
     * 用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    @ApiModelProperty("用户姓名")
    private String realName;

    /**
     * 银行账号
     */
    @NotBlank(message = "支付宝账号不能为空")
    @ApiModelProperty("支付宝账号")
    private String alipayAccount;

    /**
     * 审核状态（0：审核中；1：审核通过；2：审核不通过;3:撤回提现）
     */
    @ApiModelProperty("审核状态（0：审核中；1：审核通过；2：审核不通过）")
    private String status;

    /**
     * 审核明细
     */
    @ApiModelProperty("审核明细")
    private String verifyDetail;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remarks;

    @TableField(exist = false)
    private String payPassword;

    public UserWithdrawRecord() {}
}
