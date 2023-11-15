package com.caipiao.modules.football.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 上午9:04
 */
@Data
public class FootballMatchResult implements Serializable {
    private Long matchId;
    private String poolStatus;
    private String matchResultStatus;
    private String sectionsNo999;
}
