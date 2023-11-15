package com.caipiao.modules.company.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.company.entity.Shop;
import org.springframework.stereotype.Repository;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午2:58
 */
@Repository
public interface ShopDao extends BaseMapper<Shop> {
    Shop get();
}
