package com.caipiao.modules.app.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/3/2 下午9:37
 */
@Data
public class PayPasswordDTO implements Serializable {
    @ApiModelProperty("支付密码")
    private String payPassword;
    @ApiModelProperty("重复支付密码")
    private String rePayPassword;
    @ApiModelProperty("原支付密码")
    private String oriPayPassword;
}
