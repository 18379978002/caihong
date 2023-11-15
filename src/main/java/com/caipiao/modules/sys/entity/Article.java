package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.caipiao.common.utils.Json;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:16
 */
@Data
@TableName("cms_article")
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.IGNORED)//title重复则不插入
    @NotBlank(message = "标题不得为空")
    private String title;
    private String tags;
    private String summary;
    private String content;
    private Long category;
    private Long subCategory;
    private Date createTime;
    private Date updateTime;
    private int openCount;
    private String targetLink;
    private String outLink;
    private String image;
    private String companyId;
    private String isBanner;
    private String avatar;
    private String createStaff;
    private String createStaffName;
    private Integer orderNumber;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}
