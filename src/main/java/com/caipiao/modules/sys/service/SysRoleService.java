package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.SysRoleEntity;
import com.caipiao.common.utils.PageUtils;

import java.util.List;
import java.util.Map;


/**
 * 角色
 *
 */
public interface SysRoleService extends IService<SysRoleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Long saveRole(SysRoleEntity role);

    void update(SysRoleEntity role);

    void deleteBatch(Long[] roleIds);


    /**
     * 查询用户创建的角色ID列表
     */
    List<Long> queryRoleIdList(String createUserId);
}
