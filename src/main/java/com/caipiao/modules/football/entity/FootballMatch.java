package com.caipiao.modules.football.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午4:07
 */
@Data
@TableName("tb_football_match")
public class FootballMatch implements Serializable {

    private String matchDate;
    @TableId(type = IdType.INPUT)
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
    private int homeTeamId;
    private String homeTeamAbbName;
    private String homeTeamAllName;
    private String homeTeamAbbEnName;
    private int awayTeamId;
    private String awayTeamAbbName;
    private String awayTeamAllName;
    private String awayTeamAbbEnName;
    private String businessDate;
    private int isHot;
    private int isHide;
    private String homeRank;
    private String awayRank;
    private int baseHomeTeamId;
    private int baseAwayTeamId;
    @ApiModelProperty("总进球数")
    private String ttg;
    private Integer ttgSingle;
    @ApiModelProperty("胜平负")
    private String had;
    private Integer hadSingle;
    @ApiModelProperty("让球胜平负")
    private String hhad;
    private Integer hhadSingle;
    @ApiModelProperty("比分")
    private String crs;
    private Integer crsSingle;
    @ApiModelProperty("半全场胜平负")
    private String hafu;
    private Integer hafuSingle;
    private Integer resultSyncFlag;
    private String stopPrintTime;
}
