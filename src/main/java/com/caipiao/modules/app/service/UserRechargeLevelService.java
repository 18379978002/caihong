package com.caipiao.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.modules.app.entity.UserRechargeLevel;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午4:03
 */
public interface UserRechargeLevelService extends IService<UserRechargeLevel> {
    /**
     * 分页查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}
