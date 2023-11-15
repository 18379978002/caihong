package com.caipiao.modules.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.order.dao.OrderTicketDao;
import com.caipiao.modules.order.entity.OrderTicket;
import com.caipiao.modules.order.service.OrderTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderTicketServiceImpl extends ServiceImpl<OrderTicketDao, OrderTicket> implements OrderTicketService {
}
