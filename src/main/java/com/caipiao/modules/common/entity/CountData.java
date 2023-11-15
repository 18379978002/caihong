package com.caipiao.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("tb_count_data")
@ApiModel("数据统计")
public class CountData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * count_date
     */
    @ApiModelProperty("统计日期")
    private String countDate;

    /**
     * staff_id
     */
    @ApiModelProperty("staff_id")
    private String staffId;

    /**
     * 托管余额
     */
    @ApiModelProperty("托管余额")
    private BigDecimal userBalance;

    /**
     * 新增用户
     */
    @ApiModelProperty("新增用户")
    private Integer newUserNum;

    /**
     * 代理充值
     */
    @ApiModelProperty("代理充值")
    private BigDecimal staffRecharge;

    /**
     * 支付宝充值
     */
    @ApiModelProperty("支付宝充值")
    private BigDecimal alipayRecharge;

    /**
     * 篮球销售金额
     */
    @ApiModelProperty("篮球销售金额")
    private BigDecimal basketballSaleAmt;

    /**
     * 足球销售金额
     */
    @ApiModelProperty("足球销售金额")
    private BigDecimal footballSaleAmt;

    /**
     * 排列三销售金额
     */
    @ApiModelProperty("排列三销售金额")
    private BigDecimal plsSaleAmt;

    /**
     * 大乐透销售金额
     */
    @ApiModelProperty("大乐透销售金额")
    private BigDecimal dltSaleAmt;

    /**
     * 派奖统计
     */
    @ApiModelProperty("派奖统计")
    private BigDecimal dispatchAmt;

    public CountData() {
        this.setBasketballSaleAmt(BigDecimal.ZERO);
        this.setFootballSaleAmt(BigDecimal.ZERO);
        this.setDltSaleAmt(BigDecimal.ZERO);
        this.setPlsSaleAmt(BigDecimal.ZERO);
    }
}
