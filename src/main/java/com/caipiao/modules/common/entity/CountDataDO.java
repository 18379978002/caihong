package com.caipiao.modules.common.entity;

import lombok.Data;

/**
 * 龙虎榜业绩
 */
@Data
public class CountDataDO {
    /**
     * 销售业绩
     */
    private Integer saleSum;
    private String staffId;
    private String staffName;

    private String headimgUrl;
}
