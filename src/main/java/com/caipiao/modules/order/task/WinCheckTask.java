package com.caipiao.modules.order.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.basketball.service.BasketballMatchService;
import com.caipiao.modules.common.entity.MatchResult;
import com.caipiao.modules.common.service.MatchResultService;
import com.caipiao.modules.football.service.FootballMatchService;
import com.caipiao.modules.order.entity.Billing;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.order.entity.OrderMatch;
import com.caipiao.modules.order.entity.OrderTicket;
import com.caipiao.modules.order.service.BillingService;
import com.caipiao.modules.order.service.OrderMatchService;
import com.caipiao.modules.order.service.OrderService;
import com.caipiao.modules.order.service.OrderTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.caipiao.modules.common.util.BugReportUtil.sendMsgToDingtalk;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class WinCheckTask {
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

    @Autowired
    BillingService billingService;

    @Resource
    private ShopService shopService;
    //1
    @Scheduled(cron = "0 */20 * * * ?")
    public void winCheckTask(){
        log.info("++++++++++++++++++++开始检测是否中奖++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();
        List<Shop> shopList = shopService.list();
        for (Shop shop : shopList) {
            TenantService.putTenantContext(new TenantContext(String.valueOf(shop.getId())));
            try {
                //查询出已出票的订单
                LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
                //已出票并且未开奖状态才查询
                wrapper.eq(Order::getPlanStatus, 2).eq(Order::getWinStatus, 1).in(Order::getMatchType, 1, 2);

                List<Order> list = orderService.list(wrapper);

                if (CollUtil.isEmpty(list)) {
                    return;
                }

                flag:
                for (Order order : list) {

                    LambdaQueryWrapper<OrderMatch> wrapper2 = new LambdaQueryWrapper<>();
                    wrapper2.eq(OrderMatch::getOrderId, order.getId());
                    List<OrderMatch> orderMatches = orderMatchService.list(wrapper2);

                    Map<String, Object> mp = new HashMap<>();

                    boolean winStatus = false;

                    for (OrderMatch orderMatch : orderMatches) {
                        Integer matchType = order.getMatchType();
                        String poolStatus = "selling";
                        if (matchType == 1) {
                            poolStatus = footballMatchService.getById(orderMatch.getMatchId()).getMatchStatus();
                        }

                        if (matchType == 2) {
                            poolStatus = basketballMatchService.getById(orderMatch.getMatchId()).getMatchStatus();
                        }

                        if (!poolStatus.equals("Payout") && !poolStatus.equals("cancel")) {
                            //停止轮询，跳出此订单循环
                            continue flag;
                        }

                        //查找比赛结果
                        LambdaQueryWrapper<MatchResult> wrapper1 = new LambdaQueryWrapper<>();
                        wrapper1.eq(MatchResult::getMatchId, orderMatch.getMatchId());
                        List<MatchResult> matchResults = matchResultService.list(wrapper1);

                        if (CollUtil.isNotEmpty(matchResults)) {
                            mp.put(orderMatch.getMatchId() + "result", matchResults);
                        }

                        mp.put(orderMatch.getMatchId() + "", poolStatus);
                    }


                    //查询ticket
                    LambdaQueryWrapper<OrderTicket> wrapper1 = new LambdaQueryWrapper<>();
                    wrapper1.eq(OrderTicket::getOrderId, order.getId());

                    List<OrderTicket> orderTickets = orderTicketService.list(wrapper1);

                    for (OrderTicket orderTicket : orderTickets) {
                        String content = orderTicket.getContent();

                        //对content进行解析
                        //第一步 按照 | 进行分割
                        String[] contents = content.split("\\|");

                        double resultSp = 1d;
                        //第二步，按照#分割
                        for (String ct : contents) {
                            //1011797#CRS@1:4#45.00
                            String[] c = ct.split("#");
                            //比赛场次
                            Long matchId = Long.parseLong(c[0]);
                            //投注项
                            String betItem = c[1];
                            boolean singlePass = false;
                            String[] items = new String[]{};
                            if (StringUtils.isBlank(betItem)) {
                                singlePass = true;
                            } else {
                                items = betItem.split("@");
                            }

                            //默认比赛都结束了
                            if (mp.get(matchId + "").equals("cancel") || singlePass) {
                                //如果比赛取消，则按照1的赔率
                            } else {
                                //第四步，投注项按照@进行分割
                                String item = items[0];
                                String it = items[1];
                                //查找比赛结果
                                AtomicReference<Double> d = new AtomicReference<>(0d);
                                List<MatchResult> matchResults = (List<MatchResult>) mp.get(matchId + "result");
                                matchResults.stream().filter(matchResult -> matchResult.getCode().equals(item))
                                        .findFirst()
                                        .ifPresent(matchResult -> {
                                            //比对combination
                                            if (matchResult.getCombination().equals(it)) {
                                                d.set(Double.parseDouble(matchResult.getOdds()));
                                            }
                                        });
                                resultSp = resultSp * d.get();
                            }
                        }

                        //查看是否中奖
                        if (resultSp > 0d) {
                            //已中奖,更新中奖金额
                            double bonus = resultSp * 2 * orderTicket.getMultiple();
                            //TODO 暂时不考虑税的问题
//                        if(bonus >= 10000){
//                            bonus = bonus *0.8d;
//                        }

                            orderTicket.setBonusAmount(BigDecimal.valueOf(bonus));
                            order.setWinStatus(3);
                            order.setBonusAmount(order.getBonusAmount().add(orderTicket.getBonusAmount()));
                            winStatus = true;
                        }

                        orderTicketService.updateById(orderTicket);
                    }

                    if (!winStatus) {
                        order.setWinStatus(2);
                    }

                    orderService.updateById(order);
                    if (order.getWinStatus() == 3) {
                        processBilling(order);
                    }

                }
            } catch (Exception e) {
                sendMsgToDingtalk(e);
            }
        }
        watch.stop();
        log.info("++++++++++++++++++++结束检测是否中奖,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());
    }

    /**
     * 跟单处理
     * @param order
     */
    private void processBilling(Order order){

        if(order.getIsDocumentary() == 1){

            BigDecimal bonusAmount = order.getBonusAmount();
            //扣除佣金,查询发单
            Long billingId = order.getBillingId();
            Billing billing = billingService.getById(billingId);

            //佣金比例 为   店铺：发单人 = 4:6
            BigDecimal commissionRate = billing.getCommissionRate().multiply(BigDecimal.valueOf(0.6));

            //查询发单人的订单
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getIsBilling, 1)
                    .eq(Order::getUserId, billing.getUserId())
                    .eq(Order::getBillingId, billingId);

            Order one = orderService.getOne(wrapper);
            //更新佣金
            one.setBonusAmount(one.getBonusAmount().add(bonusAmount.multiply(commissionRate)));
            orderService.updateById(one);

            //跟单人扣除佣金
            order.setBonusAmount(bonusAmount.subtract(bonusAmount.multiply(billing.getCommissionRate())));

            orderService.updateById(order);

            //更新带红人次
            UserInfo userInfo = userInfoService.getById(billing.getUserId());

            userInfo.setRedFansNum(userInfo.getRedFansNum() + 1);

            userInfoService.updateById(userInfo);

        }
    }

}
