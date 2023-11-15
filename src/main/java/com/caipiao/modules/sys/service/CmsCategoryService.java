package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.modules.sys.entity.CmsCategory;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:34
 */
public interface CmsCategoryService extends IService<CmsCategory> {
    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage1(Map<String, Object> params);
}
