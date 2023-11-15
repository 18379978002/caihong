package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.SysUserRoleEntity;

import java.util.List;


/**
 * 用户与角色对应关系
 *
 */
public interface SysUserRoleService extends IService<SysUserRoleEntity> {

    void saveOrUpdate(String userId, List<Long> roleIdList, String companyId);

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(String userId);

    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(Long[] roleIds);
}
