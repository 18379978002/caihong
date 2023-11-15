package com.caipiao.modules.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午2:57
 */
@Data
@ApiModel("分销订单")
public class DistributionOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * pk
     */
    @ApiModelProperty("pk")
    private String id;

    /**
     * 逻辑删除标记（0：显示；1：隐藏）
     */
    @ApiModelProperty("逻辑删除标记（0：显示；1：隐藏）")
    private String delFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    private Date updateTime;

    /**
     * 分销级别（1：一级分销；2：二级分销）
     */
    @ApiModelProperty("分销级别（1：一级分销；2：二级分销）")
    private String distributionLevel;

    /**
     * 分销员id
     */
    @ApiModelProperty("分销员id")
    private String distributionUserId;

    /**
     * 订单id
     */
    @ApiModelProperty("订单id")
    private String orderId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String userId;

    /**
     * 返佣金额
     */
    @ApiModelProperty("返佣金额")
    private BigDecimal commission;

    /**
     * 佣金状态（1：冻结；2：解冻）
     */
    @ApiModelProperty("佣金状态（1：冻结；2：解冻）")
    private String commissionStatus;

    public DistributionOrder() {}
}
