package com.caipiao.modules.football;

import cn.hutool.core.util.RandomUtil;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.MD5Util;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
    @Autowired
    private  OrderService orderService;

    public static final String BASE_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    private  UserInfoService userInfoService;

    @Test
    void test(){
        UserInfo byId = userInfoService.getById("1536604788995657730");
        String password = byId.getPassword();
        System.out.println(password);
    }

    @Test
    void test1(){
        String code = RandomUtil.randomString(Constant.BASE_STR, 8);
        /*TenantService.putTenantContext(new TenantContext("1"));
        Order order = orderService.getById(140);
        System.out.println(order);*/
        System.out.println(code);
    }

}
