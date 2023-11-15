package com.caipiao.modules.app.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class RechargeCountDTO {
    private BigDecimal rechargeAmount;
    @ApiModelProperty("2、支付宝支付 3、客服代充值 4、返奖")
    private String paymentWay;

    public String getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(String paymentWay) {
        this.paymentWay = paymentWay;
    }

    public BigDecimal getRechargeAmount() {
        return null == rechargeAmount?BigDecimal.ZERO:rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
}
