package com.caipiao.modules.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:33
 */
@Data
@ApiModel("等级定义")
public class GradeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    @ApiModelProperty("id")
    private Long id;

    /**
     * 等级名称
     */
    @NotBlank(message = "等级名称不能为空", groups = AddGroup.class)
    @ApiModelProperty("等级名称")
    private String gradeName;

    /**
     * 等级图标
     */
    @ApiModelProperty("等级图标")
    private String gradeIcon;

    /**
     * 删除标识
     */
    @ApiModelProperty("删除标识")
    private String delFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 月费
     */
    @ApiModelProperty("按月付费，单位分")
    private Integer monthFee;

    /**
     * 年费
     */
    @ApiModelProperty("按年付费，单位分")
    private Integer yearFee;

    /**
     * 半年费
     */
    @ApiModelProperty("按半年付费，单位分")
    private Integer partYearFee;

    /**
     * 季度费用
     */
    @ApiModelProperty("按季度付费，单位分")
    private Integer seasonFee;

    public GradeInfo() {}
}
