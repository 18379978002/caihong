package com.caipiao.modules.app.entity;

import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午3:16
 */
@Data
@ApiModel("用户充值记录")
public class UserRechargeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * id
     */
    @ApiModelProperty("id")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String userId;

    /**
     * 充值金额
     */
    @ApiModelProperty("充值金额")
    private BigDecimal rechargeAmount;

    /**
     * 支付方式 1、现金充值；2、在线支付
     */
    @ApiModelProperty("支付类型 1、微信支付；2、支付宝支付 3、客服代充值 4、返奖")
    private String paymentWay;

    /**
     * 支付类型 1、微信支付；2、支付宝支付
     */
    @ApiModelProperty("费用类型 1、充值；2、投注 3、返奖 4、提款 5、其他")
    private Integer paymentType;

    /**
     * 支付时间
     */
    @ApiModelProperty("支付时间")
    private Date paymentTime;

    /**
     * 支付交易id
     */
    @ApiModelProperty("支付交易id")
    private String transactionId;

    /**
     * 支付状态 0、未支付 1、已支付 2、支付超时
     */
    @ApiModelProperty("支付状态 0、未支付 1、已支付 2、支付超时 3、支付失败")
    private Integer payStatus;

    /**
     * 主题
     */
    @ApiModelProperty("主题")
    private String subject;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("充值奖励金币")
    private Integer bonusAmount;

    @TableField(exist = false)
    private WxPayAppOrderResult wxPayAppOrder;

    @TableField(exist = false)
    private AlipayTradeWapPayResponse alipayTradeWapPayResponse;

    public UserRechargeRecord() {}
}
