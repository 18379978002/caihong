package com.caipiao.modules.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/3/11 下午9:53
 */
@Data
public class OrderParam implements Serializable {
    private int passType;
    private List<List<LotteryParam>> params;
    //倍数
    private Integer multiple = 1;
    private List<Integer> passTypes;
}
