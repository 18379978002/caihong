package com.caipiao.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.sys.entity.Banner;

import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2021/8/24 下午3:16
 */
public interface BannerService extends IService<Banner> {
    /**
     * 批量删除
     * @param ids
     */
    void batchDelete(List<Long> ids);
}
