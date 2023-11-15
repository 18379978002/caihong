package com.caipiao.modules.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CommonLotteryDTO {
    @ApiModelProperty("1、当天 2、本周 3、本月")
    private int period = 1;
    @ApiModelProperty("不用传")
    private List<String> userIds;

    private String staffId;
}
