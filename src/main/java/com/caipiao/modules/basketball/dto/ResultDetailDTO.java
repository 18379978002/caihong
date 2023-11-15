package com.caipiao.modules.basketball.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/27 上午10:54
 */
@Data
public class ResultDetailDTO implements Serializable {
    private String poolCode;
    private String combination;
    private String combinationDesc;

    private String odds;
    private String oddsType;

}
