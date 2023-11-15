package com.caipiao.modules.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class AmountCountDTO implements Serializable {
    private BigDecimal amount;
    private BigDecimal bonusAmount;
    private int matchType;

    public BigDecimal getAmount() {
        return null == amount? BigDecimal.ZERO: amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBonusAmount() {
        return null == bonusAmount?BigDecimal.ZERO:bonusAmount;
    }

    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }
}
