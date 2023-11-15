package com.caipiao.modules.sys.controller;

import com.caipiao.common.annotation.SysLog;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.modules.sys.entity.SysRoleEntity;
import com.caipiao.modules.sys.service.SysRoleMenuService;
import com.caipiao.modules.sys.service.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 角色列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:role:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("companyId", getCompanyId());
        //如果不是超级管理员，则只查询自己创建的角色列表
        if (!Constant.SUPER_ADMIN.toUpperCase().contains(getUserId().toUpperCase())
                && !getUser().getStaffStatus().equals(Constant.COMPANY_SUPER_STAFF_STATUS)) {
            params.put("createUserId", getUserId());
        }

        PageUtils page = sysRoleService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 角色列表
     */
    @GetMapping("/select")
    @RequiresPermissions("sys:role:select")
    public R select() {
        Map<String, Object> map = new HashMap<>();
        map.put("company_id", getCompanyId());
        //如果不是超级管理员，则只查询自己所拥有的角色列表
        if (!Constant.SUPER_ADMIN.toUpperCase().contains(getUserId().toUpperCase())
                && !getUser().getStaffStatus().equals(Constant.COMPANY_SUPER_STAFF_STATUS)) {
            map.put("create_user_id", getUserId());
        }
        List<SysRoleEntity> list = (List<SysRoleEntity>) sysRoleService.listByMap(map);

        return R.ok().put("list", list);
    }

    /**
     * 角色信息
     */
    @GetMapping("/info/{roleId}")
    @RequiresPermissions("sys:role:info")
    public R info(@PathVariable("roleId") Long roleId) {
        SysRoleEntity role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        return R.ok().put("role", role);
    }

    /**
     * 保存角色
     */
    @SysLog("保存角色")
    @PostMapping("/save")
    @RequiresPermissions("sys:role:save")
    public R save(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);
        role.setCompanyId(getCompanyId());
        role.setCreateUserId(getUserId());
        sysRoleService.saveRole(role);

        return R.ok();
    }

    /**
     * 修改角色
     */
    @SysLog("修改角色")
    @PostMapping("/update")
    @RequiresPermissions("sys:role:update")
    public R update(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);
        SysRoleEntity byId = sysRoleService.getById(role.getRoleId());
        if(!byId.getCompanyId().equals(getCompanyId())){
            throw new RRException("不允许的操作");
        }

        if(role.getRoleId() == 1L){
            return error("系统角色不可修改");
        }

        role.setCreateUserId(getUserId());
        sysRoleService.update(role);

        return R.ok();
    }

    /**
     * 删除角色
     */
    @SysLog("删除角色")
    @PostMapping("/delete")
    @RequiresPermissions("sys:role:delete")
    public R delete(@RequestBody Long[] roleIds) {

        if(Arrays.asList(roleIds).contains(1L)){
            return error("存在系统角色不可删除");
        }


        for (Long aLong : roleIds) {

            SysRoleEntity byId = sysRoleService.getById(aLong);

            if(!byId.getCompanyId().equals(getCompanyId())){
                throw new RRException("不允许的操作");
            }

        }

        sysRoleService.deleteBatch(roleIds);

        return R.ok();
    }
}
