package com.caipiao.modules.sys.service;

import com.caipiao.modules.sys.entity.SysCompStaffEntity;

import java.util.Set;

/**
 * shiro相关接口
 *
 */
public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(String userId, String companyId);

    SysCompStaffEntity queryUser(String userId);
}
