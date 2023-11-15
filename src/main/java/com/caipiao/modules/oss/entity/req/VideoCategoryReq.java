package com.caipiao.modules.oss.entity.req;

import lombok.Data;

@Data
public class VideoCategoryReq {
    private String name;
    private String companyId;
    private Integer page = 1;
    private Integer limit = 10;
    private Long id;
}
