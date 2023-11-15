package com.caipiao.modules.sys.entity.dto;

import com.caipiao.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:20
 */
@Data
public class ArticleDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String tags;
    private String summary;
    private String content;
    private Long category;
    private String categoryName;
    private String categoryCode;
    private Long subCategory;
    private String subCategoryName;
    private String subCategoryCode;
    private Date createTime;
    private Date updateTime;
    private int openCount;
    private String outLink;
    private String targetLink;
    private String image;
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
