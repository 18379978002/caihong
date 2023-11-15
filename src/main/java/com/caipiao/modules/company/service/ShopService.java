package com.caipiao.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.modules.company.entity.Shop;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:00
 */
public interface ShopService extends IService<Shop> {
    Shop get();
}
