package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.MapUtils;
import com.caipiao.modules.sys.dao.SysUserRoleDao;
import com.caipiao.modules.sys.entity.SysUserRoleEntity;
import com.caipiao.modules.sys.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 用户与角色对应关系
 *
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {

    @Override
    public void saveOrUpdate(String userId, List<Long> roleIdList, String companyId) {
        //先删除用户与角色关系
        this.removeByMap(new MapUtils().put("user_id", userId).put("company_id", companyId));

        if (roleIdList == null || roleIdList.size() == 0) {
            return;
        }

        //保存用户与角色关系
        for (Long roleId : roleIdList) {
            SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
            sysUserRoleEntity.setUserId(userId);
            sysUserRoleEntity.setRoleId(roleId);
            sysUserRoleEntity.setCompanyId(companyId);
            this.save(sysUserRoleEntity);
        }
    }

    @Override
    public List<Long> queryRoleIdList(String userId) {
        return baseMapper.queryRoleIdList(userId);
    }

    @Override
    public int deleteBatch(Long[] roleIds) {
        return baseMapper.deleteBatch(roleIds);
    }
}
