package com.caipiao.modules.permutation.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.constants.ApiConstant;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.ExtendMath;
import com.caipiao.config.TaskExcutor;
import com.caipiao.config.batisplus.TenantService;
import com.caipiao.config.batisplus.co.TenantContext;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.order.entity.OrderTicket;
import com.caipiao.modules.order.service.OrderService;
import com.caipiao.modules.order.service.OrderTicketService;
import com.caipiao.modules.permutation.entity.PermutationResult;
import com.caipiao.modules.permutation.service.PermutationResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.caipiao.modules.common.util.BugReportUtil.sendMsgToDingtalk;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class PermutationResultSyncTask {
    @Resource
    private ShopService shopService;
    @Autowired
    RestTemplate httpRestTemplate;
    @Autowired
    PermutationResultService permutationResultService;

    @Autowired
    RedisComponent redisComponent;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderTicketService orderTicketService;


    public static final String TRY_TIMES_PERMUTATION_KEY = "TRY_TIMES_PERMUTATION_KEY";

    //每天晚上9点10分，同步排列三的开奖结果
    //每天晚上9点30，计算中奖结果

    //1
    @Scheduled(cron = "0 43 21 * * ? ")
    public void permutationResultSyncTask() {

        log.info("++++++++++++++++++++开始同步排列三结果数据++++++++++++++++++++");

        try {
            JSONObject forObject = httpRestTemplate.getForObject(ApiConstant.PERMUTATION_RESULT, JSONObject.class);

            JSONObject jsonObject = forObject.getJSONObject("value").getJSONArray("list").getJSONObject(0);

            PermutationResult result = new PermutationResult();
            result.setLotteryDrawResult(jsonObject.getString("lotteryDrawResult"));
            result.setLotteryDrawNum(jsonObject.getString("lotteryDrawNum"));
            result.setLotteryDrawTime(jsonObject.getString("lotteryDrawTime"));

            permutationResultService.saveOrUpdate(result);
        } catch (Exception e) {
            sendMsgToDingtalk(e);

            Integer o = (Integer) redisComponent.get(TRY_TIMES_PERMUTATION_KEY);

            if (o == null) {
                o = 1;
            } else {
                o = o + 1;
            }

            if (o <= 5) {
                //20s后重新发起请求
                TaskExcutor.schedule(this::permutationResultSyncTask, 20, TimeUnit.SECONDS);
            }
        }

        log.info("++++++++++++++++++++结束同步排列三结果数据++++++++++++++++++++");


    }


    //1
    @Scheduled(cron = "0 03 22 * * ? ")
    public void permutationOrderResultSyncTask() {
        log.info("++++++++++++++++++++开始同步排列三中奖结果数据++++++++++++++++++++");
        List<Shop> shopList = shopService.list();
        for (Shop shop : shopList) {
            TenantService.putTenantContext(new TenantContext(String.valueOf(shop.getId())));
        //查询出已出票的订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        //已出票并且未开奖状态并且截止时间是今天才行哦
        wrapper.eq(Order::getPlanStatus, 2)
                .eq(Order::getWinStatus, 1)
                .eq(Order::getMatchType, 3)
                .apply("date_format(stop_print_time,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d')");

        List<Order> list = orderService.list(wrapper);

        if (CollUtil.isEmpty(list)) {
            return;
        }

        for (Order order : list) {
            LambdaQueryWrapper<OrderTicket> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(OrderTicket::getOrderId, order.getId());

            List<OrderTicket> orderTickets = orderTicketService.list(wrapper1);

            boolean winStatus = false;

            for (OrderTicket orderTicket : orderTickets) {
                String content = orderTicket.getContent();
                String playType = orderTicket.getPlayType();
                //代表这个为期数，第几期
                String betType = orderTicket.getBetType();
                //查询结果
                PermutationResult permutationResult = permutationResultService.getById(betType);

                if (null == permutationResult) {

                    sendMsgToDingtalk(new RRException("排列三第" + betType + "期数据同步出错，请查看"));

                    continue;
                }

                //正常的处理逻辑
                String result = permutationResult
                        .getLotteryDrawResult().replaceAll(" ", "");

                if (playType.equals("直选")) {
                    String[] split = content.split(" \\| ");
                    List<List<String>> ls = new ArrayList<>();
                    for (String s : split) {
                        ls.add(Arrays.asList(s.split(" ")));
                    }
                    List<String> all = ExtendMath.findAll(ls);
                    for (String s : all) {
                        if (s.equals(result)) {
                            //中奖
                            winStatus = setWinValue(order, orderTicket, 1040);
                        }
                    }
                }


                if (playType.equals("组选六")) {

                    int[] collect = Stream.of(content.split(" "))
                            .map(Integer::parseInt).mapToInt(Integer::intValue)
                            .toArray();

                    int[][] nchoosek = ExtendMath.nchoosek(collect, 3);

                    for (int[] ints : nchoosek) {
                        //排序对比
                        String betItem = intArrToString(ints);

                        String res = sortString(result);
                        if (betItem.equals(res)) {
                            //中奖
                            winStatus = setWinValue(order, orderTicket, 173);
                        }


                    }

                }

                if (playType.equals("组选三")) {

                    int[] collect = Stream.of(content.split(" "))
                            .map(Integer::parseInt).mapToInt(Integer::intValue)
                            .toArray();

                    int[][] nchoosek = ExtendMath.nchoosek(collect, 2);
                    for (int[] ints : nchoosek) {
                        String[] strings = intArrToStringArr(ints);
                        String res = sortString(result);

                        for (String betItem : strings) {
                            if (betItem.equals(res)) {
                                //中奖
                                winStatus = setWinValue(order, orderTicket, 346);
                            }
                        }
                    }


                }

                if (orderTicket.getBonusAmount().intValue() >= 10000) {

                    BigDecimal bonusAmount = orderTicket.getBonusAmount();
                    orderTicket.setBonusAmount(bonusAmount.multiply(BigDecimal.valueOf(0.8)));

                    order.setBonusAmount(order.getBonusAmount()
                            .subtract(bonusAmount.multiply(BigDecimal.valueOf(0.2))));
                }

                orderTicketService.updateById(orderTicket);

            }

            if (!winStatus) {
                order.setWinStatus(2);
            }

            orderService.updateById(order);

        }
        }


        log.info("++++++++++++++++++++结束同步排列三中奖结果数据++++++++++++++++++++");


    }

    private boolean setWinValue(Order order, OrderTicket orderTicket, int i) {
        order.setBonusAmount(order.getBonusAmount().add(BigDecimal.valueOf(i).multiply(BigDecimal.valueOf(orderTicket.getMultiple()))));
        orderTicket.setBonusAmount(orderTicket.getBonusAmount().add(BigDecimal.valueOf(i).multiply(BigDecimal.valueOf(orderTicket.getMultiple()))));
        order.setWinStatus(3);;
        return true;
    }

    public static void main(String[] args) {
        String content = "2 7 | 3 8 | 3 8";
        String[] split = content.split(" \\| ");

        System.out.println(JSON.toJSONString(split));
    }

    /**
     * int数组排序并转换成string
     * @param arr
     * @return
     */
    public static String intArrToString(int[] arr){

        Arrays.sort(arr);

        String result = "";

        for (int i : arr) {
            result = result + i;
        }

        return result;
    }


    /**
     * int数组排序并转换成string数组，用于排列三组选三
     * @param arr
     * @return
     */
    public static String[] intArrToStringArr(int[] arr){

        int[] result1 = new int[arr.length+1];

        result1[0] = arr[0];
        result1[1] = arr[0];
        result1[2] = arr[1];

        int[] result2 = new int[arr.length+1];

        result2[0] = arr[0];
        result2[1] = arr[1];
        result2[2] = arr[1];

        String s1 = intArrToString(result1);
        String s2 = intArrToString(result2);
        return new String[]{s1, s2};
    }

    /**
     * 将string字符串排序
     * @param str
     * @return
     */
    public static String sortString(String str){

        char[] chars = str.toCharArray();

        Arrays.sort(chars);

        return new String(chars);
    }


}
