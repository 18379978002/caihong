package com.caipiao.modules.football.dto;

import com.caipiao.modules.common.dto.League;
import com.caipiao.modules.common.dto.MatchDate;
import com.caipiao.modules.common.entity.MatchResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午5:30
 */
@Data
public class FootballValue implements Serializable {

    private Long totalCount;
    private List<League> leagueList;

    private List<MatchDate> matchDateList;

    private List<MatchInfoDTO<FootballMatchDTO>> matchInfoList;

    private List<FootballMatchResult> matchResult;
    private List<MatchResult> matchResultList;

}
