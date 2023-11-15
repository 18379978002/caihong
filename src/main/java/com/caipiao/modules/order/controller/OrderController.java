package com.caipiao.modules.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.common.utils.*;
import com.caipiao.config.SecurityConfigProperties;
import com.caipiao.modules.app.controller.AppAbstractController;
import com.caipiao.modules.common.enums.AppEnums;
import com.caipiao.modules.common.util.LotteryCalcUtils;
import com.caipiao.modules.order.dto.BonusOptimizeDTO;
import com.caipiao.modules.order.dto.OrderParam;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.order.entity.OrderTicket;
import com.caipiao.modules.order.service.OrderMatchService;
import com.caipiao.modules.order.service.OrderService;
import com.caipiao.modules.order.service.OrderTicketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaoyinandan
 * @date 2022/3/11 下午8:13
 */
@RestController
@RequestMapping("/app/order")
@Api(tags = "《app端》订单管理")
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(value = SecurityConfigProperties.class)
public class OrderController extends AppAbstractController {

    private final OrderService orderService;
    private final SecurityConfigProperties securityConfigProperties;
    private final OrderTicketService orderTicketService;
    private final OrderMatchService orderMatchService;

    @GetMapping("list")
    @ApiOperation("条件分页查询")
    public R getList(@RequestParam Map<String, Object> params){
        IPage<Order> page = new Query<Order>().getPage(params);

        // queryParam   0 全部  1 待出票  2、待开奖  3、已开奖  4、已中奖

        String queryParam = (String) params.get("queryParam");


        QueryWrapper<Order> ne = new QueryWrapper<Order>()
                .eq("user_id", getUser().getId());

        if(queryParam.equals("1")){
            //待出票。planStatus为1
            ne.eq("plan_status", AppEnums.PlanStatus.TICKET_NOT_OUT.getValue());
        }

        if(queryParam.equals("2")){
            //待开奖。planStatus为2 winStatus 为1
            ne.eq("plan_status", AppEnums.PlanStatus.TICKET_OUT.getValue())
                    .eq("win_status", AppEnums.WinStatus.LOTTERY_NOT_OUT.getValue());
        }

        if(queryParam.equals("3")){
            //已开奖。planStatus为2 winStatus 为2、3、4
            ne.eq("plan_status", AppEnums.PlanStatus.TICKET_OUT.getValue())
                    .in("win_status",
                            AppEnums.WinStatus.LOTTERY_LOSING.getValue(),
                            AppEnums.WinStatus.LOTTERY_WIN.getValue(),
                            AppEnums.WinStatus.LOTTERY_DISPATCH_OUT.getValue());
        }

        if(queryParam.equals("4")){
            //已中奖。planStatus为2 winStatus 为3、4
            ne.eq("plan_status", AppEnums.PlanStatus.TICKET_OUT.getValue())
                    .in("win_status",
                            AppEnums.WinStatus.LOTTERY_WIN.getValue(),
                            AppEnums.WinStatus.LOTTERY_DISPATCH_OUT.getValue());
        }


        IPage<Order> page1 = orderService.page(page, ne);

        return ok().put("page", new PageUtils<>(page1));
    }

    @GetMapping("getDetail/{id}")
    @ApiOperation("查询详情")
    public R getDetail(@PathVariable Long id){
        Order order = orderService.getById(id);

        if(null == order){
            return error("订单不存在");
        }

        if(!order.getUserId().equals(getUser().getId())){
            return error("您没有权限查询");
        }

        if(order.getIsDocumentary() == AppEnums.YesOrNo.YES.getValue() && order.getStopPrintTime().getTime()>System.currentTimeMillis()){
            order.setOrderTickets(new ArrayList<>());
        }else{
            LambdaQueryWrapper<OrderTicket> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderTicket::getOrderId, id);
            List<OrderTicket> list = orderTicketService.list(wrapper);
            order.setOrderTickets(list);
        }


//        LambdaQueryWrapper<OrderMatch> wrapper1 = new LambdaQueryWrapper<>();
//        wrapper1.eq(OrderMatch::getOrderId, id);
//        List<OrderMatch> list1 = orderMatchService.list(wrapper1);
//        order.setOrderMatches(list1);
        return ok().put(order);
    }

    @ApiOperation("奖金优化")
    @PostMapping("bonusOptimize")
    public R bonusOptimize(@RequestBody OrderParam param){
        Set<List<String>> lists = LotteryCalcUtils.bonusOptimize(param.getParams(), param.getPassType(), param.getMultiple());

        List<List<String>> collect = param.getParams().stream()
                .map(s1 -> s1.stream().map(s2 -> s2.getMatchNumStr() + "(" + s2.getBetItem() + "@" + s2.getSp() + ")")
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        Map<String, Object> calc = LotteryCalcUtils.calc(collect, param.getPassType());

        calc.put("maxBonus", (double)calc.get("maxBonus") * param.getMultiple());
        calc.put("minBonus", (double)calc.get("minBonus") * param.getMultiple());
        calc.put("optimize", lists);
        calc.remove("tzNumber");

        return ok().put(calc);
    }

    @ApiOperation("奖金优化<增加柱数>")
    @PostMapping("bonusOptimizeAddBetItem")
    public R bonusOptimizeAddBetItem(@RequestBody BonusOptimizeDTO param){
        Set<List<String>> lists = LotteryCalcUtils.refactor(param.getOptimize(), param.getTotalNumber()-1, param.getTotalNumber(), param.getOptimize().size());
        double sum = lists.stream().map(s -> s.get(s.size() - 1)).mapToDouble(Double::parseDouble).sum();
        double avg = sum / lists.size();
        Map<String, Object> calc = new HashMap<>();
        calc.put("optimize", lists);
        calc.put("maxBonus", param.getMaxBonus() + avg);
        calc.put("minBonus", param.getMinBonus());
        return ok().put(calc);
    }

    @ApiOperation("奖金优化<减少柱数>")
    @PostMapping("bonusOptimizeMinusBetItem")
    public R bonusOptimizeMinusBetItem(@RequestBody BonusOptimizeDTO param){
        Set<List<String>> lists = LotteryCalcUtils.refactor(param.getOptimize(), param.getTotalNumber(), param.getTotalNumber()-1, param.getOptimize().size());
        double sum = lists.stream().map(s -> s.get(s.size() - 1)).mapToDouble(Double::parseDouble).sum();
        double avg = sum / lists.size();
        Map<String, Object> calc = new HashMap<>();
        calc.put("optimize", lists);
        calc.put("maxBonus", param.getMaxBonus() - avg);
        calc.put("minBonus", param.getMinBonus());
        return ok().put(calc);
    }

    @ApiOperation("订单拆分")
    @PostMapping("orderSplit")
    public R orderSplit(@RequestBody OrderParam param){
        Map<String, Object> result = new HashMap<>();
        for (Integer passType : param.getPassTypes()) {
            Set<List<String>> lists = LotteryCalcUtils.orderSplit(param.getParams(), passType, param.getMultiple());
            result.put(passType+"", lists);
        }
        return ok().put(result);
    }

    @ApiOperation("提交订单")
    @PostMapping("submit")
    public R submit(@RequestBody Order order){

        if(StringUtils.isNotEmpty(order.getEncryptData())){
            String encryptData = order.getEncryptData();
//            log.info("数据加密的结果是：{}", encryptData);
            String decrypt = AES.decrypt(encryptData, securityConfigProperties.getEncryptKey(), securityConfigProperties.getEncryptIv());
            log.info("订单数据解密的结果是：{}", decrypt);
            order = JSONObject.parseObject(decrypt, Order.class);
        }else{
            return error("参数不正确");
        }

        String planNo = orderService.submitOrder(order, getUser());
        return ok().put(planNo);
    }

    @ApiOperation("订单支付 payType 0、余额，1、支付宝")
    @PostMapping("pay")
    public R pay(@RequestParam("planNo") String planNo, @RequestParam("payType") Integer payType, @RequestParam("payPassword")String payPassword){
        return orderService.pay(planNo, payType, getUser(), payPassword);
    }


}
