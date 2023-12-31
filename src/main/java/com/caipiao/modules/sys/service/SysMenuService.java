package com.caipiao.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.SysMenuEntity;

import java.util.List;


/**
 * 菜单管理
 *
 */
public interface SysMenuService extends IService<SysMenuEntity> {

    /**
     * 根据父菜单，查询子菜单
     * @param parentId 父菜单ID
     * @param menuIdList  用户菜单ID
     */
    List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList, String companyId);

    /**
     * 根据父菜单，查询子菜单
     * @param parentId 父菜单ID
     */
    List<SysMenuEntity> queryListParentId(Long parentId, String companyId);

    /**
     * 获取不包含按钮的菜单列表
     */
    List<SysMenuEntity> queryNotButtonList(String companyId);

    /**
     * 获取用户菜单列表
     */
    List<SysMenuEntity> getUserMenuList(String userId, String companyId);

    /**
     * 删除
     */
    void delete(Long menuId);
}
