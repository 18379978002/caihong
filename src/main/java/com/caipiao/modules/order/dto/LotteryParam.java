package com.caipiao.modules.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/3/11 下午9:36
 */
@Data
public class LotteryParam implements Serializable {
    @ApiModelProperty("比赛ID")
    private Long matchId;
    @ApiModelProperty("主队")
    private String homeTeamName;
    @ApiModelProperty("客队")
    private String awayTeamName;
    @ApiModelProperty("比赛编号，比如周一001")
    private String matchNumStr;
    //胜平负 H胜 D平 A负
    // 让球胜平负 HH(+1) HD(-1) HA(+2) 括号里面为让球数
    // 总进球数  TTG0~TTG7
    // 比分 s00s01~s05s02 s1sh为胜其他 s00s00~s03s03 s1sd为平其他 s01s00~s02s05 s1sa为负其他
    // 半全场  HH 胜胜 HD胜平 HA胜负 DH 平胜 DD平平 DA平负 AH负胜 AD 负平 AA 负负
    @ApiModelProperty("投注项")
    private String betItem;
    @ApiModelProperty("赔率")
    private String sp;
}
