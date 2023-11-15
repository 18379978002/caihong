package com.caipiao.modules.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午8:27
 */
@Data
@ApiModel("用户充值")
public class UserRechargeDTO implements Serializable {

    @ApiModelProperty("充值等级ID")
    private String rechargeLevelId;

    @ApiModelProperty("VIP时间类型 1月费、2季度费、3半年费、4年费")
    private Integer vipTime;

    @NotNull(message = "支付方式不能为空")
    @ApiModelProperty("支付方式 1微信、2支付宝")
    private Integer paymentWay;

    private String planNo;

    @ApiModelProperty("充值金额")
    private BigDecimal rechargeMoney;

    private String quitUrl;
    private String returnUrl;
}
