package com.caipiao.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.modules.app.dao.UserRechargeLevelDao;
import com.caipiao.modules.app.entity.UserRechargeLevel;
import com.caipiao.modules.app.service.UserRechargeLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午4:03
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRechargeLevelServiceImpl extends ServiceImpl<UserRechargeLevelDao, UserRechargeLevel> implements UserRechargeLevelService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserRechargeLevel> page = this.page(
                new Query<UserRechargeLevel>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }
}
