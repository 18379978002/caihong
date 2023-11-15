package com.caipiao.modules.sys.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.MapUtils;
import com.caipiao.modules.sys.dao.SysMenuDao;
import com.caipiao.common.utils.Constant;
import com.caipiao.modules.sys.dao.SysUserDao;
import com.caipiao.modules.sys.entity.SysMenuEntity;
import com.caipiao.modules.sys.service.SysMenuService;
import com.caipiao.modules.sys.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList, String companyId) {
        List<SysMenuEntity> menuList = queryListParentId(parentId, companyId);
        if (menuIdList == null) {
            return menuList;
        }

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        for (SysMenuEntity menu : menuList) {
            if (menuIdList.contains(menu.getMenuId())) {
                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId, String companyId) {
        return baseMapper.queryListParentId(parentId, companyId);
    }

    @Override
    public List<SysMenuEntity> queryNotButtonList(String companyId) {
        return baseMapper.queryNotButtonList(companyId);
    }

    @Override
    public List<SysMenuEntity> getUserMenuList(String userId, String companyId) {
        //系统管理员，拥有最高权限
        if (Constant.SUPER_ADMIN.toUpperCase().contains(userId.toUpperCase())) {
            return getAllMenuList(null, companyId);
        }

        //用户菜单列表
        List<Long> menuIdList = sysUserDao.queryAllMenuId(userId);
        return getAllMenuList(menuIdList, companyId);
    }

    @Override
    public void delete(Long menuId) {
        //删除菜单
        this.removeById(menuId);
        //删除菜单与角色关联
        sysRoleMenuService.removeByMap(new MapUtils().put("menu_id", menuId));
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenuEntity> getAllMenuList(List<Long> menuIdList, String companyId) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryListParentId(0L, menuIdList, companyId);
        //递归获取子菜单
        getMenuTreeList(menuList, menuIdList, companyId);

        return menuList;
    }

    /**
     * 递归
     */
    private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList, String companyId) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

        for (SysMenuEntity entity : menuList) {
            //目录
            if (entity.getType() == Constant.MenuType.CATALOG.getValue()) {
                entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList, companyId), menuIdList, companyId));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }
}
