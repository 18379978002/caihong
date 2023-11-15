package com.caipiao.modules.permutation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_permutation_result")
public class PermutationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    /**
     * lottery_draw_num
     */
    private String lotteryDrawNum;

    /**
     * lottery_draw_result
     */
    private String lotteryDrawResult;

    private String lotteryDrawTime;

    public PermutationResult() {}
}
