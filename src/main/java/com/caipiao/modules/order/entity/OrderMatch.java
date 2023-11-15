package com.caipiao.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@TableName("tb_order_match")
@Data
@ApiModel("order_match")
public class OrderMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * plan_no
     */
    @ApiModelProperty("plan_no")
    private Long orderId;

    /**
     * home_team
     */
    @ApiModelProperty("home_team")
    private String homeTeam;

    /**
     * away_team
     */
    @ApiModelProperty("away_team")
    private String awayTeam;

    /**
     * 比如 周一001
     */
    @ApiModelProperty("比如 周一001")
    private String matchNumStr;

    /**
     * half_score
     */
    @ApiModelProperty("half_score")
    private String halfScore;

    /**
     * final_score
     */
    @ApiModelProperty("final_score")
    private String finalScore;

    /**
     * match_id
     */
    @ApiModelProperty("match_id")
    private Long matchId;

    public OrderMatch() {}
}
