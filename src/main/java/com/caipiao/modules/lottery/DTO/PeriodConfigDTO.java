package com.caipiao.modules.lottery.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 追期配置
 */
@Data
public class PeriodConfigDTO implements Serializable {
    @ApiModelProperty("追期期数")
    private Integer times;
    @ApiModelProperty("中奖后停止追期   0否  1是")
    private Integer stopAfterBonus;
    @ApiModelProperty("第几期")
    private Integer whichPeriod;

    @ApiModelProperty("下期是否停止 1是 0否")
    private int stopNextPeriod;
}
