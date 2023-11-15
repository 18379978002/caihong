package com.caipiao.modules.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.order.dao.BillingTicketDao;
import com.caipiao.modules.order.entity.BillingTicket;
import com.caipiao.modules.order.service.BillingTicketService;
import org.springframework.stereotype.Service;

@Service
public class BillingTicketServiceImpl extends ServiceImpl<BillingTicketDao, BillingTicket> implements BillingTicketService {
}
