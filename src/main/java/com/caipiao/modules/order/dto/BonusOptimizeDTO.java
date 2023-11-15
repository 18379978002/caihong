package com.caipiao.modules.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class BonusOptimizeDTO implements Serializable {
    private Set<List<String>> optimize;
    private Integer totalNumber;


    private double maxBonus;
    private double minBonus;
}
