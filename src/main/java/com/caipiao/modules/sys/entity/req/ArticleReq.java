package com.caipiao.modules.sys.entity.req;

import lombok.Builder;
import lombok.Data;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:21
 */
@Data
@Builder
public class ArticleReq {
    private String title;
    private String categoryCode;
    private String subCategoryCode;
    private String companyId;
    private String isBanner;
    private String categoryName;
    private String belongApp;
}
