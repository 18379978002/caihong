package com.caipiao.modules.sys.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysCompStaffVO implements Serializable {
    private String staffId;
    private String staffName;
    private String avatar;
    private String deptName;
    private String position;

    private String userId;
}
