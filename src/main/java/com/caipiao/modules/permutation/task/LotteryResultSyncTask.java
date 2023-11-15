package com.caipiao.modules.permutation.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.caipiao.modules.permutation.entity.LotteryResult;
import com.caipiao.modules.permutation.entity.PrizeLevelResult;
import com.caipiao.modules.permutation.service.LotteryResultService;
import com.caipiao.modules.permutation.service.PrizeLevelResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.caipiao.modules.common.util.BugReportUtil.sendMsgToDingtalk;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class LotteryResultSyncTask {
    @Resource
    private ShopService shopService;

    @Autowired
    RestTemplate httpRestTemplate;
    @Autowired
    LotteryResultService lotteryResultService;

    @Autowired
    RedisComponent redisComponent;
    @Autowired
    PrizeLevelResultService prizeLevelResultService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderTicketService orderTicketService;

    public static final String TRY_TIMES_PERMUTATION_KEY = "TRY_TIMES_PERMUTATION_KEY_LOTTERY";

    //每天晚上9点10分，同步排列三的开奖结果
    //每天晚上9点30，计算中奖结果

    //1
    @Scheduled(cron = "0 47 21 * * ? ")
    public void lotteryResultSyncTask(){

        log.info("++++++++++++++++++++开始同步大乐透结果数据++++++++++++++++++++");

        try {
            JSONObject forObject = httpRestTemplate.getForObject(ApiConstant.LOTTERY_RESULT, JSONObject.class);

            JSONObject jsonObject = forObject.getJSONObject("value").getJSONArray("list").getJSONObject(0);

            LotteryResult result = new LotteryResult();
            result.setLotteryDrawResult(jsonObject.getString("lotteryDrawResult"));
            result.setLotteryDrawNum(jsonObject.getString("lotteryDrawNum"));
            result.setLotteryDrawTime(jsonObject.getString("lotteryDrawTime"));

            lotteryResultService.saveOrUpdate(result);

            //保存中奖金额
            JSONArray prizeLevelList = jsonObject.getJSONArray("prizeLevelList");
            for (int i = 0; i < prizeLevelList.size(); i++) {

                if(prizeLevelList.getJSONObject(i).getInteger("awardType") == 0){
                    String prizeLevel = prizeLevelList.getJSONObject(i).getString("prizeLevel");
                    String stakeAmount = prizeLevelList.getJSONObject(i).getString("stakeAmount").replaceAll(",","");

                    LambdaUpdateWrapper<PrizeLevelResult> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.eq(PrizeLevelResult::getLotteryDrawNum, result.getLotteryDrawNum())
                            .eq(PrizeLevelResult::getPrizeLevel, prizeLevel);

                    PrizeLevelResult res = new PrizeLevelResult();
                    res.setLotteryDrawNum(result.getLotteryDrawNum());
                    res.setPrizeLevel(prizeLevel);
                    res.setStakeAmount(new BigDecimal(stakeAmount));

                    prizeLevelResultService.saveOrUpdate(res, wrapper);
                }
            }


        }catch (Exception e){
            sendMsgToDingtalk(e);

            Integer o = (Integer) redisComponent.get(TRY_TIMES_PERMUTATION_KEY);

            if(o == null){
                o = 1;
            }else{
                o = o + 1;
            }

            if(o<= 5){
                //20s后重新发起请求
                TaskExcutor.schedule(this::lotteryResultSyncTask, 20, TimeUnit.SECONDS);
            }
        }

        log.info("++++++++++++++++++++结束同步大乐透结果数据++++++++++++++++++++");


    }

//1
    //是够中奖同步
    @Scheduled(cron = "0 13 22 * * ? ")
    public void lotteryOrderResultSyncTask(){
        log.info("++++++++++++++++++++开始同步大乐透中奖结果数据++++++++++++++++++++");
        List<Shop> shopList = shopService.list();
        for (Shop shop : shopList) {
            TenantService.putTenantContext(new TenantContext(String.valueOf(shop.getId())));
            //查询出已出票的订单
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            //已出票并且未开奖状态才查询
            wrapper.eq(Order::getPlanStatus, 2)
                    .eq(Order::getWinStatus, 1)
                    .eq(Order::getMatchType, 4)
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
                    //代表这个为期数，第几期
                    String betType = orderTicket.getBetType();
                    //查询结果
                    LotteryResult lotteryResult = lotteryResultService.getById(betType);

                    if (null == lotteryResult) {

                        sendMsgToDingtalk(new RRException("大乐透第" + betType + "期数据同步出错，请查看"));

                        this.lotteryResultSyncTask();

                        continue;
                    }

                    //正常的处理逻辑
                    String result = lotteryResult.getLotteryDrawResult();
                    String[] results = result.split(" ");
                    //前区
                    int[] frontSeg = new int[5];
                    //后区
                    int[] backSeg = new int[2];

                    for (int i = 0; i < results.length; i++) {
                        if (i < 5) {
                            frontSeg[i] = Integer.parseInt(results[i]);
                        } else {
                            if (i == 5) {
                                backSeg[0] = Integer.parseInt(results[i]);
                            }

                            if (i == 6) {
                                backSeg[1] = Integer.parseInt(results[i]);
                            }
                        }
                    }

                    Arrays.sort(frontSeg);
                    Arrays.sort(backSeg);

                    JSONObject jsonObject = JSONObject.parseObject(content);
                    JSONArray frontSectionList = jsonObject.getJSONArray("frontSectionList");
                    JSONArray backSectionList = jsonObject.getJSONArray("backSectionList");
                    List<Integer> fsl = frontSectionList.toJavaList(Integer.class);
                    List<Integer> bsl = backSectionList.toJavaList(Integer.class);

                    int[] fslInts = fsl.stream().mapToInt(Integer::intValue).toArray();
                    int[] bslInts = bsl.stream().mapToInt(Integer::intValue).toArray();

                    int[][] fslnchoosek = ExtendMath.nchoosek(fslInts, 5);
                    int[][] bslnchoosek = ExtendMath.nchoosek(bslInts, 2);

                    //查询结果数据
                    LambdaQueryWrapper<PrizeLevelResult> wrapper2 = new LambdaQueryWrapper<>();
                    wrapper2.eq(PrizeLevelResult::getLotteryDrawNum, betType);
                    List<PrizeLevelResult> list1 = prizeLevelResultService.list(wrapper2);

                    for (int[] ints : fslnchoosek) {
                        for (int[] ints1 : bslnchoosek) {
                            //校验前区以及后区
                            Arrays.sort(ints);
                            Arrays.sort(ints1);

                            int i = countRight(frontSeg, ints);
                            int i2 = countRight(backSeg, ints1);
                            String prizeLevel = getPrizeLevel(i, i2);

                            //计算奖金
                            BigDecimal bonusAmount = getBonusAmount(list1, order.getAppend(), prizeLevel, orderTicket.getMultiple());
                            if (bonusAmount.compareTo(BigDecimal.ZERO) > 0) {
                                //已中奖
                                winStatus = true;
                                order.setBonusAmount(order.getBonusAmount().add(bonusAmount));
                                orderTicket.setBonusAmount(orderTicket.getBonusAmount().add(bonusAmount));
                                order.setWinStatus(3);
                                ;
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

        log.info("++++++++++++++++++++结束同步大乐透中奖结果数据++++++++++++++++++++");
    }

    /**
     * 统计正确个数
     * @param arr1 原始数组
     * @param arr2 待比较数组
     * @return
     */
    private int countRight(int[] arr1, int[] arr2){
        int count = 0;
        for (int i = 0; i < arr2.length; i++) {
            for (int i1 : arr1) {
                if(arr2[i] == i1){
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * 计算几等奖
     * @param i1
     * @param i2
     * @return
     */
    private String getPrizeLevel(int i1, int i2){
        if(i1 == 5 && i2 == 2){
            return "一等奖";
        }

        if(i1 == 5 && i2 == 1){
            return "二等奖";
        }

        if(i1 == 5 && i2 == 0){
            return "三等奖";
        }

        if(i1 == 4 && i2 == 2){
            return "四等奖";
        }

        if(i1 == 4 && i2 == 1){
            return "五等奖";
        }

        if(i1 == 3 && i2 == 2){
            return "六等奖";
        }

        if(i1 == 4 && i2 == 0){
            return "七等奖";
        }

        if(i1 == 3 && i2 == 1){
            return "八等奖";
        }

        if(i1 == 2 && i2 == 2){
            return "八等奖";
        }


        if(i1 == 0 && i2 == 2){
            return "九等奖";
        }
        if(i1 == 1 && i2 == 2){
            return "九等奖";
        }
        if(i1 == 2 && i2 == 1){
            return "九等奖";
        }
        if(i1 == 3 && i2 == 0){
            return "九等奖";
        }

        return "error";

    }

    /**
     * 计算奖金
     * @param results
     * @param append
     * @param prizeLevel
     * @param multiple
     * @return
     */
    private BigDecimal getBonusAmount(List<PrizeLevelResult> results, int append, String prizeLevel, int multiple){
        AtomicReference<BigDecimal> bonusAmount = new AtomicReference<>(BigDecimal.ZERO);
        results.stream().filter(prizeLevelResult -> prizeLevelResult.getPrizeLevel().equals(prizeLevel))
                .findFirst()
                .ifPresent(pr->{
                    //代表中奖
                    bonusAmount.set(pr.getStakeAmount().multiply(BigDecimal.valueOf(multiple)));
                    if(append == 1){

                        if(pr.getPrizeLevel().equalsIgnoreCase("一等奖") || pr.getPrizeLevel().equalsIgnoreCase("二等奖")){
                            //追加，则
                            BigDecimal bigDecimal = bonusAmount.get();
                            //追加的中奖金额为
                            int i = pr.getStakeAmount().multiply(BigDecimal.valueOf(0.8)).intValue();
                            bonusAmount.set(bigDecimal.add(BigDecimal.valueOf(i).multiply(BigDecimal.valueOf(multiple))));
                        }


                    }
                });
        return bonusAmount.get();
    }

    public static void main(String[] args) {
        int[] arr1 = {1,2,3,4,5};
        int[] arr2 = {1,2,3,4,5};

        for (int i = 0; i < arr1.length; i++) {
            for (int i1 : arr2) {
                System.out.println("arr1[i]="+arr1[i] + ",arr2="+i1);
                if(i1 == 3 ){
                    //System.out.println("arr1[i]="+arr1[i] + ",arr2="+i1);
                    break;
                }
            }
        }

    }

}
