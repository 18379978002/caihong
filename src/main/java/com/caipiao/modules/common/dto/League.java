package com.caipiao.modules.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午4:09
 */
@Data
public class League implements Serializable {
    private String leagueId;
    private String leagueName;
    @ApiModelProperty("简称")
    private String leagueNameAbbr;
}
