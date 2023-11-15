package com.caipiao.modules.basketball.dto;

import com.caipiao.modules.basketball.entity.BasketballMatch;
import com.caipiao.modules.common.dto.League;
import com.caipiao.modules.common.dto.MatchDate;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午9:27
 */
@Data
public class BasketballMatchData implements Serializable {
    private Long totalCount = 0L;
    private List<League> leagueList = new ArrayList<>();
    private List<MatchDate> matchDateList = new ArrayList<>();
    private Map<String, List<BasketballMatch>> matchList = new HashMap<>();
}
