package com.caipiao.modules.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午4:11
 */
@Data
public class MatchDate implements Serializable {
    private String businessDate;
    private String businessDateCn;
}
