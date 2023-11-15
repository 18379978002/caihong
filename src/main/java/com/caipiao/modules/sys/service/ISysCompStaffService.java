package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.req.StaffReq;
import com.caipiao.modules.sys.entity.vo.SysCompStaffVO;

import java.util.List;

public interface ISysCompStaffService extends IService<SysCompStaffEntity> {

    PageUtils deptStaffPage(String deptId, Integer pageIndex, Integer pageSize, String keyWord, String companyId);

    PageUtils deptStaffPage(Integer pageIndex, Integer pageSize, String keyWord, List<String> staffIds);

    void setRole(StaffReq req);

    SysCompStaffVO getByStaffId(String staffId);

    String genNextUserId();

    SysCompStaffEntity getByUserId(String id);
}
