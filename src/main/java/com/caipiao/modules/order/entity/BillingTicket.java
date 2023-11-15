package com.caipiao.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("发单票表")
@TableName("tb_billing_ticket")
public class BillingTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * billing_id
     */
    @ApiModelProperty("billing_id")
    private Long billingId;

    /**
     * 投注类型
     */
    @ApiModelProperty("投注类型")
    private String betType;

    /**
     * 玩法
     */
    @ApiModelProperty("玩法")
    private String playType;

    /**
     * 过关类型
     */
    @ApiModelProperty("过关类型")
    private String passType;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    private String content;

    /**
     * 倍数
     */
    @ApiModelProperty("倍数")
    private Integer multiple;

    /**
     * 投注内容
     */
    @ApiModelProperty("投注内容")
    private String betContent;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /**
     * 中奖金额
     */
    @ApiModelProperty("中奖金额")
    private BigDecimal bonusAmount;

    public BillingTicket() {}
}
