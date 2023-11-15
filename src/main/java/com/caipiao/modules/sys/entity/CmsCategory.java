package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.caipiao.common.utils.Json;
import com.caipiao.modules.sys.constant.AppEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

/**
 * cms文章
 */
@Data
@TableName("cms_category")
public class CmsCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.IGNORED)//title重复则不插入
    @NotBlank(message = "目录不得为空")
    private String categoryName;
    private String categoryCode;
    private Long parentId;
    private Integer topLevel;
    private String companyId;
    //1 内部分类 2、外部链接
    private String type;
    private String outTargetLink;
    private String icon;
    //所属应用
    @NotBlank(message = "所属应用不得为空")
    private String belongApp;

    @TableField(exist = false)
    private String belongAppName;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }

    public String getBelongAppName(){
        return Objects.requireNonNull(AppEnum.find(Integer.parseInt(this.belongApp))).getAppName();
    }
}
