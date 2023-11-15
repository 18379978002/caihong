package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.DeptEntity;
import com.caipiao.modules.sys.entity.vo.TreeSelect;

import java.util.List;

public interface ISysDeptService extends IService<DeptEntity> {
    List<TreeSelect> buildDeptTreeSelect(List<DeptEntity> deptEntities);
    List<DeptEntity> buildDeptTree(List<DeptEntity> deptEntities);

    /**
     * 查询用户部门信息
     * @param dingUserId
     * @return
     */
    List<DeptEntity> queryUserDept(String dingUserId);
}
