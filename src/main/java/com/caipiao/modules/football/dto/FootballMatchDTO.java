package com.caipiao.modules.football.dto;

import com.caipiao.modules.common.dto.PoolList;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午5:17
 */
@Data
public class FootballMatchDTO implements Serializable {
    private String matchDate;
    private String matchName;

    private Long matchId;

    /**
     * 例：5001
     * 组成规则，5为星期几，后面的001代表当天第几场比赛
     */
    private Long matchNum;
    /**
     * 例：周五001
     */
    private String matchNumStr;
    /**
     * 例：Selling 售卖中  Payout 已结束
     */
    private String matchStatus;

    private String matchTime;

    private String matchWeek;

    private Long leagueId;
    private String leagueAllName;
    private String leagueAbbName;
    private String leagueCode;

    /**
     * 售卖状态 1可售卖 0不可买
     */
    private Integer sellStatus;
    private String homeTeamCode;
    private String awayTeamCode;
    private String lineNum;
    private String backColor;
    private Integer homeTeamId;
    private String homeTeamAbbName;
    private String homeTeamAllName;
    private String homeTeamAbbEnName;
    private Integer awayTeamId;
    private String awayTeamAbbName;
    private String awayTeamAllName;
    private String awayTeamAbbEnName;
    private String businessDate;
    private String remark;
    private Integer isHot;
    private Integer isHide;
    private String homeRank;
    private String awayRank;
    private String groupName;
    private Integer baseHomeTeamId;
    private Integer baseAwayTeamId;
    private Integer bettingAllUp;

    private Integer bettingSingle;
    private List<PoolList> poolList;
    private Crs crs;
    private Had had;
    private Hhad hhad;
    private Hafu hafu;
    private Ttg ttg;
}
