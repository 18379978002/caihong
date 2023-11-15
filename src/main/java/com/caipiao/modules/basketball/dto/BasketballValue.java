package com.caipiao.modules.basketball.dto;

import com.caipiao.modules.common.dto.League;
import com.caipiao.modules.common.dto.MatchDate;
import com.caipiao.modules.common.entity.MatchResult;
import com.caipiao.modules.football.dto.MatchInfoDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午5:30
 */
@Data
public class BasketballValue implements Serializable {

    private Long totalCount;
    private List<League> leaguesList;

    private List<MatchDate> matchDateList;

    private List<MatchInfoDTO<BasketballMatchDTO>> matchInfoList;

    private List<BasketballMatchResult> matchResult;
    private List<MatchResult> matchResultList;

}
