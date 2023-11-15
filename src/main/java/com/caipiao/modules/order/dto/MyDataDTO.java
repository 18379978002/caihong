package com.caipiao.modules.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MyDataDTO implements Serializable {
    @ApiModelProperty("已中奖")
    private int winNum;
    @ApiModelProperty("待开奖")
    private int waitOpenNum;
    @ApiModelProperty("待出票")
    private int waitTicketNum;
    @ApiModelProperty("账户余额")
    private BigDecimal balance;
    @ApiModelProperty("今日中奖金额")
    private BigDecimal todayBonus;
    @ApiModelProperty("用户标签")
    private String userTag;
    @ApiModelProperty("是否有分管用户Id")
    private Boolean hasSuperiorManageId;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickName;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

}
