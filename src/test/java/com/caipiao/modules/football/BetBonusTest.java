package com.caipiao.modules.football;

import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.basketball.service.BasketballMatchService;
import com.caipiao.modules.common.service.MatchResultService;
import com.caipiao.modules.football.service.FootballMatchService;
import com.caipiao.modules.order.service.OrderMatchService;
import com.caipiao.modules.order.service.OrderService;
import com.caipiao.modules.order.service.OrderTicketService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class BetBonusTest {

    @Autowired
    OrderService orderService;

    @Autowired
    FootballMatchService footballMatchService;

    @Autowired
    BasketballMatchService basketballMatchService;

    @Autowired
    UserInfoService userInfoService;
    @Autowired
    OrderTicketService orderTicketService;
    @Autowired
    OrderMatchService orderMatchService;
    @Autowired
    MatchResultService matchResultService;


    @Test
    void test(){

    }

    public static void main(String[] args) {
        double d = 0.000000d;
        System.out.println(d == 0d);
    }

}
