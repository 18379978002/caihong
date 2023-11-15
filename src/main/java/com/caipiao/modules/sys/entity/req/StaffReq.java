package com.caipiao.modules.sys.entity.req;

import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StaffReq implements Serializable {
    private List<Long> roleIds;
    private String staffId;
    private String deptId;
    private String keyWord;
    private String companyId;
    private List<String> staffIds;
    private SysCompStaffEntity staff;
}
