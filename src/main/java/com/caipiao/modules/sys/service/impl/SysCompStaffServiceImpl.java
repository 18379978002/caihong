package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.sys.dao.StaffDeptDao;
import com.caipiao.modules.sys.dao.SysCompStaffDao;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.req.StaffReq;
import com.caipiao.modules.sys.entity.vo.SysCompStaffVO;
import com.caipiao.modules.sys.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SuppressWarnings("all")
public class SysCompStaffServiceImpl extends
        ServiceImpl<SysCompStaffDao, SysCompStaffEntity> implements ISysCompStaffService {

    private final StaffDeptDao staffDeptDao;
    private final ISysDeptService sysDeptService;
    private final IStaffDeptService staffDeptService;
    private final SysUserRoleService sysUserRoleService;
    private final RedisComponent redisComponent;
    private final CompanyAppConfigService companyAppConfigService;


    @Override
    public PageUtils deptStaffPage(String deptId, Integer pageIndex, Integer pageSize, String keyWord, String companyId) {
        IPage<SysCompStaffEntity> page1 = new Page<>(pageIndex, pageSize);

        StaffReq req = new StaffReq();
        req.setCompanyId(companyId);
        req.setKeyWord(keyWord);

        IPage<SysCompStaffVO> vos = baseMapper.queryPage(page1, req);

        return new PageUtils(vos);
    }

    @Override
    public PageUtils deptStaffPage(Integer pageIndex, Integer pageSize, String keyWord, List<String> staffIds) {
        IPage<SysCompStaffEntity> page1 = new Page<>(pageIndex, pageSize);

        StaffReq req = new StaffReq();
        req.setStaffIds(staffIds);
        req.setKeyWord(keyWord);

        IPage<SysCompStaffVO> vos = baseMapper.queryPage(page1, req);

        return new PageUtils(vos);
    }

    @Override
    public void setRole(StaffReq req) {
        //校验权限
        SysCompStaffEntity staff = req.getStaff();

        SysCompStaffEntity byId = this.getById(req.getStaffId());

        if(!staff.getCompanyId().equals(byId.getCompanyId())){
            throw new RRException("暂无权限！");
        }

        //设置用户角色
        sysUserRoleService.saveOrUpdate(req.getStaffId(), req.getRoleIds(), staff.getCompanyId());

    }

    @Override
    public SysCompStaffVO getByStaffId(String staffId) {
        return this.baseMapper.getByStaffId(staffId);
    }

    @Override
    public String genNextUserId() {
        return this.baseMapper.getNextId();
    }

    @Override
    public SysCompStaffEntity getByUserId(String userId) {
        if(StringUtils.isNotBlank(userId)){
            return baseMapper.selectByUserId(userId);
        }else {
            return null;
        }
    }
}
