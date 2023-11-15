package com.caipiao.modules.order.task;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class RevokeOrderTask {

    @Autowired
    private OrderService orderService;
    @Resource
    private ShopService shopService;

    //1
    @Scheduled(cron = "0 0/5 * * * ?")
    public void revokeOrderTask(){
        List<Shop> shopList = shopService.list();
        for (Shop shop : shopList) {
            TenantService.putTenantContext(new TenantContext(String.valueOf(shop.getId())));
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getPlanStatus, 1)
                    .apply("date_format(stop_print_time,'%Y-%m-%d %H:%i:%S')<=date_format(now(),'%Y-%m-%d %H:%i:%S')");

            List<Order> list = orderService.list(wrapper);

            if(CollUtil.isNotEmpty(list)){
                for (Order order : list) {
                    orderService.cancelOrder(order.getId()+"", "超出售票截止时间未出票系统自动撤回订单");
                }
            }
        }

    }



}
