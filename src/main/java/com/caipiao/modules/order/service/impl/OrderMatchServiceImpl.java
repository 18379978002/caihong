package com.caipiao.modules.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.order.dao.OrderMatchDao;
import com.caipiao.modules.order.entity.OrderMatch;
import com.caipiao.modules.order.service.OrderMatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderMatchServiceImpl extends ServiceImpl<OrderMatchDao, OrderMatch> implements OrderMatchService {
}
