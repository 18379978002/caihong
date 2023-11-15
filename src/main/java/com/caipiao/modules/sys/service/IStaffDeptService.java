package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.StaffDeptEntity;

import java.util.List;

public interface IStaffDeptService extends IService<StaffDeptEntity> {

    List<String> listDeptIdsByUserId(String staffId);

}
