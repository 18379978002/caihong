package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.MD5Util;
import com.caipiao.modules.sys.dao.SysCompanyDao;
import com.caipiao.modules.sys.entity.*;
import com.caipiao.modules.sys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/20 10:18
 */
@Service
public class SysCompanyServiceImpl extends ServiceImpl<SysCompanyDao, SysCompany> implements SysCompanyService {
    @Autowired
    CompanyAppConfigService companyAppConfigService;
    @Autowired
    ISysCompStaffService sysCompStaffService;
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    SysUserRoleService sysUserRoleService;
    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Autowired
    CompanyAppService companyAppService;


    @Override @Transactional
    public String newCompany(SysCompany company) {

        return null;
    }

    @Override
    public SysCompany queryDetail(String id) {

        SysCompany company = this.getById(id);

        LambdaQueryWrapper<CompanyAppConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanyAppConfig::getCorpId, company.getCorpId())
                .eq(CompanyAppConfig::getCompanyId, company.getId()).last("limit 1");

        CompanyAppConfig one = companyAppConfigService.getOne(wrapper);

        company.setAgentId(one.getAgentId());
        company.setSecret(one.getSecret());

        LambdaQueryWrapper<SysCompStaffEntity> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SysCompStaffEntity::getCompanyId, id)
                .eq(SysCompStaffEntity::getStaffStatus, Constant.COMPANY_SUPER_STAFF_STATUS).last("limit 1");
        SysCompStaffEntity staff = sysCompStaffService.getOne(wrapper1);

        List<SysMenuEntity> userMenuList = sysMenuService.getUserMenuList(staff.getStaffId(), id);
        List<Long> collect = userMenuList.stream().map(sysMenuEntity -> sysMenuEntity.getMenuId()).collect(Collectors.toList());

        company.setSuperAdminAccount(staff.getStaffId());

        company.setSuperAdminMenus(collect);

        return company;
    }

    @Override @Transactional
    public Object editCompany(SysCompany company) {

        company.setUpdateTime(new Date());
        this.updateById(company);

        //更新配置文件
        LambdaQueryWrapper<CompanyAppConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanyAppConfig::getCompanyId, company.getId())
                .eq(CompanyAppConfig::getCorpId, company.getCorpId());
        CompanyAppConfig config = companyAppConfigService.getOne(wrapper);
        config.setCorpId(company.getCorpId());

        config.setCompanyId(company.getId());
        config.setAgentId(company.getAgentId());
        config.setSecret(company.getSecret());
        companyAppConfigService.updateById(config);

        //更新企业权限信息
        List<SysMenuEntity> userMenuList = sysMenuService.getUserMenuList(company.getSuperAdminAccount(), company.getId());

        List<Long> tmpList = new ArrayList<>();
        List<Long> newList = new ArrayList<>();
        //旧菜单
        List<Long> collect = userMenuList.stream().map(SysMenuEntity::getMenuId).collect(Collectors.toList());
        //新菜单
        List<Long> superAdminMenus = company.getSuperAdminMenus();

        for (Long superAdminMenu : collect) {
            if(!superAdminMenus.contains(superAdminMenu)){
                //回收的权限
                tmpList.add(superAdminMenu);
            }
        }

        for(Long me : superAdminMenus){
            if(!collect.contains(me)){
                //新权限
                newList.add(me);
            }
        }

        if(!CollectionUtils.isEmpty(tmpList)){
            //遍历公司每个角色的权限，剔除回收的权限
            LambdaQueryWrapper<SysRoleEntity> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(SysRoleEntity::getCompanyId, company.getId());
            List<SysRoleEntity> list = sysRoleService.list(wrapper1);

            for (SysRoleEntity sysRoleEntity : list) {
                List<Long> longs = sysRoleMenuService.queryMenuIdList(sysRoleEntity.getRoleId());

                for(Long ls : longs){
                    if(tmpList.contains(ls)){
                        LambdaQueryWrapper<SysRoleMenuEntity> wrapper2 = new LambdaQueryWrapper<>();
                        wrapper2.eq(SysRoleMenuEntity::getMenuId, ls).eq(SysRoleMenuEntity::getRoleId, sysRoleEntity.getRoleId());
                        sysRoleMenuService.remove(wrapper2);
                    }
                }
            }
        }

        if(!CollectionUtils.isEmpty(newList)){
            //给超级管理员赋予新权限
            List<Long> longs = sysUserRoleService.queryRoleIdList(company.getSuperAdminAccount());

            for (Long aLong : newList) {
                SysRoleMenuEntity roleMenu = new SysRoleMenuEntity();
                roleMenu.setCompanyId(company.getId());
                roleMenu.setMenuId(aLong);
                roleMenu.setRoleId(longs.get(0));
                sysRoleMenuService.save(roleMenu);
            }
        }

        //更新app
        updateAppList(company);



        return null;
    }

    private void updateAppList(SysCompany company){
        LambdaQueryWrapper<CompanyApp> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(CompanyApp::getCompanyId, company.getId()).eq(CompanyApp::getLevel, 1);
        List<CompanyApp> list = companyAppService.list(wrapper1);

        Long parentId;
        if(CollectionUtils.isEmpty(list)){
            //创建一级分类
            CompanyApp app = new CompanyApp();
            app.setCompanyId(company.getId());
            app.setAppName("办公应用");
            app.setAppCode("BGYY");
            app.setParentId(0L);
            app.setLevel(1);
            app.setState(1);
            app.setSortNumber(1);
            companyAppService.save(app);

            parentId = app.getId();

        }else{
            parentId = list.get(0).getId();
        }

        if(!CollectionUtils.isEmpty(company.getApps())){

            //删除二级分类
            LambdaQueryWrapper<CompanyApp> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(CompanyApp::getCompanyId, company.getId()).eq(CompanyApp::getLevel, 2);
            companyAppService.remove(wrapper2);

            //保存单位app列表
            for (CompanyApp app : company.getApps()) {

                if(app.getAppUrl().contains("news/?companyId")){
                    app.setAppUrl("http://plkj.dongkonggroup.com/news/?companyId="+company.getId());
                }

                if(app.getAppUrl().contains("books/?shelfNo")){

                }

                app.setCompanyId(company.getId());
                app.setLevel(2);
                app.setId(null);
                app.setParentId(parentId);
                companyAppService.save(app);
            }


        }
    }

    private void validateAccountName(String accountName){
        SysCompStaffEntity byId = sysCompStaffService.getById(accountName);

        if(null != byId){
            throw new RRException("账号已存在");
        }
    }

    private Long saveCompanyRole(SysCompany company){
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleName("超级管理员");
        role.setCreateUserId(company.getSuperAdminAccount());
        role.setCompanyId(company.getId());
        role.setMenuIdList(company.getSuperAdminMenus());
        return sysRoleService.saveRole(role);

    }

    private void saveAccount(SysCompany company){
        SysCompStaffEntity staff = new SysCompStaffEntity();
        staff.setStaffStatus("COMPANY");
        staff.setStaffPasswd(MD5Util.getMD5AndSalt(company.getSuperAdminPassword()));
        staff.setStaffId(company.getSuperAdminAccount());
        staff.setStaffName("管理员");
        staff.setCompanyId(company.getId());
        sysCompStaffService.save(staff);
    }
}
