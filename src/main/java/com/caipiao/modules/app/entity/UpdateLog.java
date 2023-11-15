package com.caipiao.modules.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_update_log")
public class UpdateLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * version_number
     */
    @ApiModelProperty("版本号")
    @NotBlank(message = "版本号不能为空", groups = AddGroup.class)
    private String versionNumber;

    /**
     * update_content
     */
    @ApiModelProperty("更新内容")
    @NotBlank(message = "更新内容不能为空", groups = AddGroup.class)
    private String updateContent;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * download_url
     */
    @ApiModelProperty("下载地址")
    @NotBlank(message = "下载地址不能为空", groups = AddGroup.class)
    private String downloadUrl;

}
