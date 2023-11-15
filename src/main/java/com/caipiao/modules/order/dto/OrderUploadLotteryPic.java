package com.caipiao.modules.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/3/18 下午9:45
 */
@Data
public class OrderUploadLotteryPic implements Serializable {
    /**
     * 订单id
     */
    private String planNo;
    /**
     * 图片
     */
    private List<String> lotteryPics;
    /**
     * 订单金额
     */
    private BigDecimal amount;
}
