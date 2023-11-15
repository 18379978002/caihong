package com.caipiao.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.modules.app.entity.GradeInfo;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:34
 */
public interface GradeInfoService extends IService<GradeInfo> {
    /**
     * 分页查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}
