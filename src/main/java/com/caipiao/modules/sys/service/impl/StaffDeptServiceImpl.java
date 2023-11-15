package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.sys.dao.StaffDeptDao;
import com.caipiao.modules.sys.entity.StaffDeptEntity;
import com.caipiao.modules.sys.service.IStaffDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffDeptServiceImpl extends ServiceImpl<StaffDeptDao, StaffDeptEntity> implements IStaffDeptService {

    @Override
    public List<String> listDeptIdsByUserId(String staffId) {
        QueryWrapper<StaffDeptEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", staffId);
        List<StaffDeptEntity> list = this.baseMapper.selectList(queryWrapper);
        if(null == list || list.isEmpty()){
            return null;
        }

        return list.stream().map(StaffDeptEntity::getDeptId).collect(Collectors.toList());
    }
}
