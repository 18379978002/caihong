package com.caipiao.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("发单表")
@TableName("tb_billing")
public class Billing implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    private String avatar;

    /**
     * 订单状态 1下单 2已接单 3已出票
     */
    @ApiModelProperty("订单状态 1下单 2已接单 3已出票")
    private Integer planStatus;

    /**
     * 中奖状态 1未开奖 2未中奖 3已中奖
     */
    @ApiModelProperty("中奖状态 1未开奖 2未中奖 3已中奖")
    private Integer winStatus;

    /**
     * 过关类型
     */
    @ApiModelProperty("过关类型")
    private String passType;

    /**
     * 下单时间
     */
    @ApiModelProperty("下单时间")
    private Date createTime;

    /**
     * 停止接单时间
     */
    @ApiModelProperty("停止接单时间")
    private Date stopPrintTime;

    /**
     * 取票状态
     */
    @ApiModelProperty("取票状态")
    private Integer taskStatus;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickName;

    /**
     * 订单金额
     */
    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    /**
     * 倍数
     */
    @ApiModelProperty("倍数")
    private Integer multiple;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String userId;

    /**
     * 问题
     */
    @ApiModelProperty("问题")
    private String issueResult;

    /**
     * 中奖金额
     */
    @ApiModelProperty("中奖金额")
    private BigDecimal bonusAmount;

    /**
     * 1足球 2篮球 3...
     */
    @ApiModelProperty("1足球 2篮球 3...")
    private Integer matchType;

    /**
     * 是否奖金优化
     */
    @ApiModelProperty("是否奖金优化")
    private Integer isOptimize;

    /**
     * 预计奖金
     */
    @ApiModelProperty("预计奖金")
    private BigDecimal planBonus;

    /**
     * 投注类型
     */
    @ApiModelProperty("投注类型")
    private String playType;

    /**
     * 发单内容
     */
    @ApiModelProperty("发单内容")
    private String billingContent;

    /**
     * 跟单起始金额
     */
    @ApiModelProperty("跟单起始金额")
    private BigDecimal startOrderAmount;

    /**
     * 佣金比例
     */
    @ApiModelProperty("佣金比例")
    private BigDecimal commissionRate;

    /**
     * 订单id
     */
    @ApiModelProperty("订单id")
    private Long orderId;

    /**
     * 发单图片
     */
    @ApiModelProperty("发单图片")
    private String billingPic;

    /**
     * 跟单热度
     */
    @ApiModelProperty("跟单热度")
    private Integer billingHeat;
    @TableField(exist = false)
    private List<Order> orders = new ArrayList<>();
    @TableField(exist = false)
    private List<BillingTicket> tickets = new ArrayList<>();


    public Billing() {}
}
