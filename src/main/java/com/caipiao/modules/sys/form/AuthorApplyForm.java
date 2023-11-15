package com.caipiao.modules.sys.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午8:13
 */
@Data
public class AuthorApplyForm implements Serializable {

    @ApiModelProperty("审核结果 1 已通过 2、已拒绝")
    @NotNull(message = "审核结果不能为空")
    private Integer auditStatus;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("ID")
    @NotBlank(message = "ID不能为空")
    private String authorApplyId;
}
