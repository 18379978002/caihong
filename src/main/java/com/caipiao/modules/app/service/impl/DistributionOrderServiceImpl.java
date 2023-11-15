package com.caipiao.modules.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.app.dao.DistributionOrderDao;
import com.caipiao.modules.app.entity.DistributionOrder;
import com.caipiao.modules.app.service.DistributionOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:00
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DistributionOrderServiceImpl extends ServiceImpl<DistributionOrderDao, DistributionOrder> implements DistributionOrderService {
}
