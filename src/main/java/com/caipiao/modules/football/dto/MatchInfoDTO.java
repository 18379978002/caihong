package com.caipiao.modules.football.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午5:33
 */
@Data
public class MatchInfoDTO<T> implements Serializable {
    private String businessDate;
    private Long matchCount;
    private List<T> subMatchList;
    private String weekday;
}
