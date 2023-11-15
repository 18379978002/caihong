package com.caipiao.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.annotation.SysLog;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.R;
import com.caipiao.modules.sys.entity.SysMenuEntity;
import com.caipiao.modules.sys.service.ShiroService;
import com.caipiao.modules.sys.service.SysMenuService;
import com.caipiao.modules.sys.service.SysRoleMenuService;
import com.caipiao.modules.sys.service.SysUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统菜单
 *
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private ShiroService shiroService;

    @Autowired
    SysUserRoleService sysUserRoleService;
    @Autowired
    SysRoleMenuService sysRoleMenuService;

    /**
     * 导航菜单
     */
    @GetMapping("/nav")
    public R nav() {
        List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(getUserId(), getCompanyId());
        Set<String> permissions = shiroService.getUserPermissions(getUserId(), getCompanyId());
        return R.ok().put("menuList", menuList).put("permissions", permissions);
    }

    /**
     * 所有菜单列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:menu:list")
    public List<SysMenuEntity> list() {
        List<SysMenuEntity> menuList;
        if(Constant.SUPER_ADMIN.contains(getUserId())){
            menuList = sysMenuService.list();
        }else {
            List<Long> roleIdList = sysUserRoleService.queryRoleIdList(getUserId());

            List<Long> menuIds = new ArrayList<>();

            for (Long roleId : roleIdList) {
                List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
                menuIds.addAll(menuIdList);
            }

            List<Long> collect = menuIds.stream().distinct().collect(Collectors.toList());

            LambdaQueryWrapper<SysMenuEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SysMenuEntity::getMenuId, collect);
            menuList = sysMenuService.list(wrapper);
        }


        for (SysMenuEntity sysMenuEntity : menuList) {
            SysMenuEntity parentMenuEntity = sysMenuService.getById(sysMenuEntity.getParentId());
            if (parentMenuEntity != null) {
                sysMenuEntity.setParentName(parentMenuEntity.getName());
            }
        }

        return menuList;
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @GetMapping("/select")
    @RequiresPermissions("sys:menu:select")
    public R select() {
        //查询列表数据
        List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList(getCompanyId());

        //添加顶级菜单
        SysMenuEntity root = new SysMenuEntity();
        root.setMenuId(0L);
        root.setName("一级菜单");
        root.setParentId(-1L);
        root.setOpen(true);
        menuList.add(root);

        return R.ok().put("menuList", menuList);
    }

    /**
     * 菜单信息
     */
    @GetMapping("/info/{menuId}")
    @RequiresPermissions("sys:menu:info")
    public R info(@PathVariable("menuId") Long menuId) {
        SysMenuEntity menu = sysMenuService.getById(menuId);
        return R.ok().put("menu", menu);
    }

    /**
     * 保存
     */
    @SysLog("保存菜单")
    @PostMapping("/save")
    @RequiresPermissions("sys:menu:save")
    public R save(@RequestBody SysMenuEntity menu) {

        //非root用户无法创建菜单
        if(!isShopManager()){
            return error("暂无权限");
        }

        menu.setCompanyId("10000");

        //数据校验
        verifyForm(menu);

        sysMenuService.save(menu);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改菜单")
    @PostMapping("/update")
    @RequiresPermissions("sys:menu:update")
    public R update(@RequestBody SysMenuEntity menu) {
        //非root用户无法创建菜单
        if(!isShopManager()){
            return error("暂无权限");
        }
        //数据校验
        verifyForm(menu);

        sysMenuService.updateById(menu);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除菜单")
    @PostMapping("/delete/{menuId}")
    @RequiresPermissions("sys:menu:delete")
    public R delete(@PathVariable("menuId") long menuId) {
        //非root用户无法创建菜单
        if(!isShopManager()){
            return error("暂无权限");
        }

        //判断是否有子菜单或按钮
        List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId, getCompanyId());
        if (menuList.size() > 0) {
            return R.error("请先删除子菜单或按钮");
        }

        sysMenuService.delete(menuId);

        return R.ok();
    }

    /**
     * 验证参数是否正确
     */
    private void verifyForm(SysMenuEntity menu) {
        if (StringUtils.isBlank(menu.getName())) {
            throw new RRException("菜单名称不能为空");
        }

        if (menu.getParentId() == null) {
            throw new RRException("上级菜单不能为空");
        }

        //菜单
        if (menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (StringUtils.isBlank(menu.getUrl())) {
                throw new RRException("菜单URL不能为空");
            }
        }

        //上级菜单类型
        int parentType = Constant.MenuType.CATALOG.getValue();
        if (menu.getParentId() != 0) {
            SysMenuEntity parentMenu = sysMenuService.getById(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == Constant.MenuType.CATALOG.getValue() ||
            menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (parentType != Constant.MenuType.CATALOG.getValue()) {
                throw new RRException("上级菜单只能为目录类型");
            }
            return;
        }

        //按钮
        if (menu.getType() == Constant.MenuType.BUTTON.getValue()) {
            if (parentType != Constant.MenuType.MENU.getValue()) {
                throw new RRException("上级菜单只能为菜单类型");
            }
            return;
        }
    }
}
