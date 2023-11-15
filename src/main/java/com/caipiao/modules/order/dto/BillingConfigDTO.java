package com.caipiao.modules.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BillingConfigDTO implements Serializable {
    @ApiModelProperty("跟单内容")
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
     * 发单图片
     */
    @ApiModelProperty("发单图片")
    private String billingPic;
}
