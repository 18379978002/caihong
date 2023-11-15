package com.caipiao.modules.app.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午4:00
 */
@Data
public class WithdrawReq implements Serializable {
    @NotBlank(message = "提现记录ID不能为空")
    private String recordId;
    //审核状态（1：审核通过；2：审核不通过）
    @NotBlank(message = "审核结果不能为空")
    @ApiModelProperty("审核状态（1：审核通过；2：审核不通过）")
    private String result;
    private String remark;
}
