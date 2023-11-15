package com.caipiao.modules.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/27 下午9:45
 */
@Data
public class LotteryBonusDTO implements Serializable {
    private List<String> passType;
    private List<List<String>> matches;
    //倍数
    private Integer multiple = 1;
}
