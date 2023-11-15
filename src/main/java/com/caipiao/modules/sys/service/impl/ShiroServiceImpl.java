package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.Constant;
import com.caipiao.modules.sys.dao.SysCompStaffDao;
import com.caipiao.modules.sys.dao.SysMenuDao;
import com.caipiao.modules.sys.dao.SysUserDao;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.SysMenuEntity;
import com.caipiao.modules.sys.service.ShiroService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysCompStaffDao sysCompstaffDao;

    @Override
    public Set<String> getUserPermissions(String userId, String companyId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if (Constant.SUPER_ADMIN.toUpperCase().contains(userId.toUpperCase())) {
            LambdaQueryWrapper<SysMenuEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenuEntity::getCompanyId, companyId);
            List<SysMenuEntity> menuList = sysMenuDao.selectList(wrapper);
            permsList = new ArrayList<>(menuList.size());
            for (SysMenuEntity menu : menuList) {
                permsList.add(menu.getPerms());
            }
        } else {
            permsList = sysUserDao.queryAllPerms(userId, companyId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();

        for (String perms : permsList) {
            if (StringUtils.isBlank(perms)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysCompStaffEntity queryUser(String userId) {
        return sysCompstaffDao.selectById(userId);
    }
}
