package com.caipiao.modules.common.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LotteryDTO implements Serializable {
    @ApiModelProperty("前区")
    private List<String> frontSectionList;
    @ApiModelProperty("后区")
    private List<String> backSectionList;

}
