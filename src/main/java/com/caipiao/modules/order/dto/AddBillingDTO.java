package com.caipiao.modules.order.dto;

import lombok.Data;

@Data
public class AddBillingDTO {
    private Long billingId;
    //跟单倍数
    private int multiple;
}
