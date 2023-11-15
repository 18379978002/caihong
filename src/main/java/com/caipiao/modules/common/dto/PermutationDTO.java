package com.caipiao.modules.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PermutationDTO {

    @ApiModelProperty("类型 1、直选  2、组三  3、组六")
    private Integer type;
    @ApiModelProperty("直选数据")
    private List<List<String>> directSelectData;

    @ApiModelProperty("组三数据")
    private int[] comb3Data;

    @ApiModelProperty("组六数据")
    private int[] comb6Data;

}
