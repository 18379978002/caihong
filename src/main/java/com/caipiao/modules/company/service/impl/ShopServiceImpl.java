package com.caipiao.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.company.dao.ShopDao;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:00
 */
@Service
@Slf4j
public class ShopServiceImpl extends ServiceImpl<ShopDao, Shop> implements ShopService {


    @Override
    public Shop get() {
        return baseMapper.get();
    }
}
