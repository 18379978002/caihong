package com.caipiao.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Data
@TableName("sys_video_category")
public class VideoCategory implements Serializable {
    @TableId(type = IdType.AUTO)
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Long id;
    @NotBlank(message = "分类名不能为空", groups = AddGroup.class)
    private String name;
    @NotNull(message = "排序不能为空", groups = AddGroup.class)
    private Integer orderNum;
    private String companyId;
}
