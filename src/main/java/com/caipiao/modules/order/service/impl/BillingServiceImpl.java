package com.caipiao.modules.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.BeanUtils;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.order.dao.BillingDao;
import com.caipiao.modules.order.dto.AddBillingDTO;
import com.caipiao.modules.order.entity.*;
import com.caipiao.modules.order.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl extends ServiceImpl<BillingDao, Billing> implements BillingService {

    private final BillingTicketService billingTicketService;
    private final UserInfoService userInfoService;
    private final OrderService orderService;
    private final OrderTicketService orderTicketService;
    private final OrderMatchService orderMatchService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addBilling(AddBillingDTO dto, UserInfo user) {

        Billing billing = this.getById(dto.getBillingId());

        if(System.currentTimeMillis()>billing.getStopPrintTime().getTime()){
            throw new RRException("已过了截止时间");
        }

        Order order = new Order();
        BeanUtils.copyProperties(billing, order);
        order.setId(null);
        order.setUserId(user.getId());
        order.setNickName(user.getNickName());
        order.setPlanStatus(0);
        order.setPlanNo("P" + IdUtil.getSnowflake(1,1).nextIdStr());
        order.setWinStatus(1);
        order.setAmount(billing.getStartOrderAmount().multiply(BigDecimal.valueOf(dto.getMultiple())));
        order.setMultiple(dto.getMultiple());
        //是否跟单为是
        order.setIsDocumentary(1);
        order.setBillingId(dto.getBillingId());
        orderService.save(order);

        //保存ticket
        List<BillingTicket> list = getBillingTickets(dto.getBillingId());

        List<OrderTicket> collect = list.stream().map(billingTicket -> {
            OrderTicket ticket = new OrderTicket();
            BeanUtils.copyProperties(billingTicket, ticket);
            ticket.setId(null);
            ticket.setOrderId(order.getId());
            ticket.setAmount(ticket.getAmount().multiply(BigDecimal.valueOf(dto.getMultiple())));
            //倍数特殊处理，主订单倍数按照传进来的倍数进行处理
            ticket.setMultiple(ticket.getAmount().intValue()/2);
            return ticket;
        }).collect(Collectors.toList());

        orderTicketService.saveBatch(collect);

        //保存比赛场次
        LambdaQueryWrapper<OrderMatch> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(OrderMatch::getOrderId, billing.getOrderId());
        List<OrderMatch> list1 = orderMatchService.list(wrapper1);

        for (OrderMatch orderMatch : list1) {
            orderMatch.setOrderId(order.getId());
            orderMatch.setId(null);
        }

        orderMatchService.saveBatch(list1);

        return order.getId();
    }

    @Override
    public Billing queryDetail(Long billingId, UserInfo user) {
        Billing billing = this.getById(billingId);
        if(billing.getUserId().equals(user.getId()) || System.currentTimeMillis()>billing.getStopPrintTime().getTime()){
            //查询方案详情
            billing.setTickets(getBillingTickets(billingId));
        }

        //查询跟单人员
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getBillingId, billingId)
                //只查出已付款的
                .in(Order::getPlanStatus, 1, 2)
                .eq(Order::getIsDocumentary, 1);
        List<Order> list1 = orderService.list(wrapper);
        billing.setOrders(list1);

        return billing;
    }

    private List<BillingTicket> getBillingTickets(Long billingId){
        LambdaQueryWrapper<BillingTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillingTicket::getBillingId, billingId);
        return billingTicketService.list(wrapper);
    }

}
