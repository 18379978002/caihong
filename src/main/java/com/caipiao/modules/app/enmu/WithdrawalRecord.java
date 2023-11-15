package com.caipiao.modules.app.enmu;

import lombok.Getter;

@Getter
public enum WithdrawalRecord {

    /**
     * 申请中
     */
    S,
    /**
     * 已结算
     */
    J,
    /**
     * 已撤销
     */
    C
}
