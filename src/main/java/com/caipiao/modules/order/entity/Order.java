package com.caipiao.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.modules.lottery.DTO.PeriodConfigDTO;
import com.caipiao.modules.order.dto.BillingConfigDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("订单表")
@TableName("tb_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @ApiModelProperty("订单编号")
    private String planNo;

    /**
     * 订单状态 1下单 2已接单 3已出票
     */
    @ApiModelProperty("订单状态 0未支付 1下单 2已出票 3已撤单 4、超时取消")
    private Integer planStatus;

    /**
     * 中奖状态 1未开奖 2未中奖 3已中奖
     */
    @ApiModelProperty("中奖状态 1未开奖 2未中奖 3已中奖 4、已派奖")
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
     * 出票时间
     */
    @ApiModelProperty("出票时间")
    private Date printTime;

    /**
     * 取票状态
     */
    @ApiModelProperty("取票状态 1未取票 2已取票")
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
     * 是否跟单
     */
    @ApiModelProperty("是否跟单")
    private Integer isDocumentary;

    /**
     * 跟单ID
     */
    @ApiModelProperty("跟单ID")
    private Long billingId;

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
     * 彩票图片
     */
    @ApiModelProperty("彩票图片")
    private String lotteryPics;

    /**
     * 1足球 2篮球 3.排列三 4、超级大乐透
     */
    @ApiModelProperty("1足球 2篮球 3.排列三 4、超级大乐透")
    private Integer matchType;
    @ApiModelProperty("是否奖金优化")
    private Integer isOptimize;
    @ApiModelProperty("预计奖金")
    private BigDecimal planBonus;

    @TableField(exist = false)
    private List<OrderTicket> orderTickets;

    @TableField(exist = false)
    private List<OrderMatch> orderMatches;
    @TableField(exist = false)
    private String payPassword;


    //如果是排列三，投注类型有   直选  组三 组六
    @ApiModelProperty("投注类型")
    private String playType;

    @ApiModelProperty("是否追加 1是 0否")
    private int append;

    @ApiModelProperty("是否追期 1是 0否")
    private int period;
    @ApiModelProperty("是否停止追期 1是 0否")
    private int stopPeriod;

    @TableField(exist = false)
    @ApiModelProperty("追期配置")
    private PeriodConfigDTO periodConfigDTO;

    /**
     * 是否发单
     */
    @ApiModelProperty("是否发单")
    private int isBilling;

    @TableField(exist = false)
    @ApiModelProperty("发单配置")
    private BillingConfigDTO billingConfigDTO;

    private String periodConfig;

    @TableField(exist = false)
    @ApiModelProperty("加密数据")
    private String encryptData;

    public Order() {}
}
