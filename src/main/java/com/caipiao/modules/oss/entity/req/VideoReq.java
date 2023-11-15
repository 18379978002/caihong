package com.caipiao.modules.oss.entity.req;

import lombok.Data;

@Data
public class VideoReq {
    private String companyId;
    private Long categoryId;
    private String originalFileName;
    private Integer isHot;
    private Integer videoType;
    private Integer isCollect;
    private Integer page = 1;
    private Integer limit = 10;
}
