package com.caipiao.modules.permutation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@TableName("tb_prize_level_result")
public class PrizeLevelResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * lottery_draw_num
     */
    @ApiModelProperty("lottery_draw_num")
    private String lotteryDrawNum;

    /**
     * prize_level
     */
    @ApiModelProperty("prize_level")
    private String prizeLevel;

    /**
     * stake_amount
     */
    @ApiModelProperty("stake_amount")
    private BigDecimal stakeAmount;
}
