package com.caipiao.modules.sys.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.dao.UserInfoDao;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller公共组件
 * @author xiaoyinandan
 */
@Component
public abstract class AbstractController extends R {

    @Autowired
    UserInfoDao userInfoDao;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected SysCompStaffEntity getUser() {
        SysCompStaffEntity staff = (SysCompStaffEntity) SecurityUtils.getSubject().getPrincipal();
        staff.setCompanyId("10000");
        return staff;
    }

    protected String getUserId() {
        return getUser().getStaffId();
    }

    protected String getCompanyId() {
        return "10000";
    }

    protected Boolean isShopManager(){
        return getUser().getPosition().equals("super");
    }

    protected List<String> queryStaffManageList(){
        return queryStaffManageList(getUserId());
    }

    protected List<String> queryStaffManageList(String staffId){
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getManageStaffId, staffId);
        List<UserInfo> staffManages = userInfoDao.selectList(wrapper);

        if(CollUtil.isEmpty(staffManages)){
            return new ArrayList<>();
        }

        return staffManages.stream()
                .map(UserInfo::getId)
                .collect(Collectors.toList());
    }

}
