package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.sys.dao.StaffManageDao;
import com.caipiao.modules.sys.entity.StaffManage;
import com.caipiao.modules.sys.service.StaffManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StaffManageServiceImpl extends ServiceImpl<StaffManageDao, StaffManage> implements StaffManageService {
}
