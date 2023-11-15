package com.caipiao.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caipiao.modules.basketball.dto.ResultDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 上午9:41
 */
@Data
public class MatchResult implements Serializable {
    private String code;
    private String combination;
    private String goalLine;
    private Long matchId;
    private String odds;
    private String oddsType;
    @TableId(type = IdType.INPUT)
    private Long poolId;
    private String poolTotals;
    private String refundStatus;

    @TableField(exist = false)
    private String sectionsNo999;
    @TableField(exist = false)
    private Object mnlResultList;
    @TableField(exist = false)
    private Object hiloResultList;
    @TableField(exist = false)
    private Object hdcResultList;
    @TableField(exist = false)
    private Object wnmResultList;


}
