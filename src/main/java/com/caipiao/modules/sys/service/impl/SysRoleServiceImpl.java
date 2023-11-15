package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.modules.sys.dao.SysCompStaffDao;
import com.caipiao.modules.sys.dao.SysRoleDao;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.SysRoleEntity;
import com.caipiao.modules.sys.service.SysRoleMenuService;
import com.caipiao.modules.sys.service.SysRoleService;
import com.caipiao.modules.sys.service.SysUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 角色
 *
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    SysCompStaffDao sysCompStaffDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String roleName = (String) params.get("roleName");

        IPage<SysRoleEntity> page = this.page(
            new Query<SysRoleEntity>().getPage(params),
            new QueryWrapper<SysRoleEntity>()
                .like(StringUtils.isNotBlank(roleName), "role_name", roleName)
                    .eq("company_id", params.get("companyId"))
                    //隐藏ID为1的角色
                .ne("role_id", 1L)
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveRole(SysRoleEntity role) {
        role.setCreateTime(new Date());
        this.save(role);

        //检查权限是否越权
        checkPrems(role);

        //保存角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList(), role.getCompanyId());

        return role.getRoleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleEntity role) {
        this.updateById(role);

        //检查权限是否越权
        checkPrems(role);

        //更新角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList(), role.getCompanyId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] roleIds) {
        //删除角色
        this.removeByIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        sysRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        sysUserRoleService.deleteBatch(roleIds);
    }


    @Override
    public List<Long> queryRoleIdList(String createUserId) {
        return baseMapper.queryRoleIdList(createUserId);
    }

    /**
     * 检查权限是否越权
     */
    private void checkPrems(SysRoleEntity role) {
        //如果不是超级管理员，则需要判断角色的权限是否超过自己的权限
        SysCompStaffEntity staff = sysCompStaffDao.selectById(role.getCreateUserId());

        if (!staff.getStaffStatus().equals(Constant.SUPER_STAFF_STATUS) && !staff.getStaffStatus().equals(Constant.COMPANY_SUPER_STAFF_STATUS)) {
            return;
        }
    }
}
