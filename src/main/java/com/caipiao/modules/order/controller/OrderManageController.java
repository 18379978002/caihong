package com.caipiao.modules.order.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.common.utils.R;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import com.caipiao.modules.common.enums.AppEnums;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.order.dto.AmountCountDTO;
import com.caipiao.modules.order.dto.OrderUploadLotteryPic;
import com.caipiao.modules.order.entity.Order;
import com.caipiao.modules.order.entity.OrderMatch;
import com.caipiao.modules.order.entity.OrderTicket;
import com.caipiao.modules.order.service.OrderMatchService;
import com.caipiao.modules.order.service.OrderService;
import com.caipiao.modules.order.service.OrderTicketService;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/3/11 下午8:13
 */
@RestController
@RequestMapping("/manage/order")
@Api(tags = "《管理端》订单管理")
@Slf4j
@RequiredArgsConstructor
public class OrderManageController extends AbstractController {

    private final OrderService orderService;
    private final OrderTicketService orderTicketService;
    private final OrderMatchService orderMatchService;
    private final ShopService shopService;

    @GetMapping("list")
    @ApiOperation("条件分页查询")
    public R getList(@RequestParam Map<String, Object> params){
        IPage<Order> page = new Query<Order>().getPage(params);

        Object winStatus = params.get("winStatus");
        Object planStatus = params.get("planStatus");
        Object matchType = params.get("matchType");
        String userId = (String)params.get("userId");

        List<String> userIds = new ArrayList<>();

        if(!isShopManager()){
            userIds = queryStaffManageList();
            if(CollUtil.isEmpty(userIds)){
                return ok();
            }
        }

        QueryWrapper<Order> ne = new QueryWrapper<Order>()
                .eq(winStatus != null && StringUtils.isNotBlank((CharSequence) winStatus), "win_status", winStatus)
                .eq(planStatus != null && StringUtils.isNotBlank((CharSequence) planStatus), "plan_status", planStatus)
                .eq(StringUtils.isNotBlank(userId), "user_id", userId)
                .eq(matchType != null && StringUtils.isNotBlank((CharSequence) matchType), "match_type", matchType);

        if(CollUtil.isNotEmpty(userIds) && StringUtils.isEmpty(userId)){
            ne.in("user_id", userIds);
        }

        ne.in("plan_status", 1,2);

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

        LambdaQueryWrapper<OrderTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderTicket::getOrderId, id);
        List<OrderTicket> list = orderTicketService.list(wrapper);
        order.setOrderTickets(list);
        LambdaQueryWrapper<OrderMatch> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(OrderMatch::getOrderId, id);
        List<OrderMatch> list1 = orderMatchService.list(wrapper1);
        order.setOrderMatches(list1);
        return ok().put(order);
    }

    @PostMapping("querySaleData")
    @ApiOperation("查询销售业绩")
    public R querySaleData(@RequestBody CommonLotteryDTO dto){
        if(isShopManager() && StringUtils.isBlank(dto.getStaffId())){
            dto.setUserIds(null);
        }else{
            String staffId = getUserId();
            if(isShopManager()){
                staffId = dto.getStaffId();
            }
            List<String> collection = queryStaffManageList(staffId);
            if(CollUtil.isEmpty(collection)){
                //0代表没有权限
                return ok().put(new AmountCountDTO());
            }else{
                dto.setUserIds(collection);
            }
        }
        List<AmountCountDTO> amountCountDTO = orderService.queryAmountCount(dto);
        return ok().put(amountCountDTO);
    }

    @PostMapping("uploadLotteryPics")
    @ApiOperation("上传彩票或者接单")
    public R uploadLotteryPics(@RequestBody OrderUploadLotteryPic pic){
        String userId = getUserId();
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getShopPhone,userId);
        Shop shop = shopService.getOne(wrapper);
        Order order = orderService.getById(pic.getPlanNo());
        if(null == order){
            return error("订单不存在");
        }
        if (shop.getBalance().compareTo(order.getAmount().multiply(new BigDecimal(Constant.COMMISSION_RATE)))<0) {
            throw new RRException("大哥 没钱了 打不出票 该充钱了");
        }
        //状态判断，只有待接单或者已支付状态才能够进行接单操作
        if(order.getPlanStatus() != AppEnums.PlanStatus.TICKET_NOT_OUT.getValue()){
            return error("订单状态不允许接单");
        }
        if(CollUtil.isNotEmpty(pic.getLotteryPics())){
            order.setLotteryPics(JSON.toJSONString(pic.getLotteryPics()));
        }
        //已接单
        order.setPlanStatus(AppEnums.PlanStatus.TICKET_OUT.getValue());
        order.setPrintTime(new Date());
        orderService.updateById(order);

        BigDecimal subtract = shop.getBalance().subtract(order.getAmount().multiply(new BigDecimal(Constant.COMMISSION_RATE)));
        shop.setBalance(subtract);
        shopService.updateById(shop);

        return ok();
    }

    @PostMapping("cancelOrder")
    @ApiOperation("撤单")
    public R cancelOrder(@RequestParam(value = "planNo", required = true) String planNo,
                         @RequestParam(value = "reason", required = false) String reason){
        orderService.cancelOrder(planNo, reason);
        return ok();
    }

    @PostMapping("dispatchBonus")
    @ApiOperation("派奖")
    public R dispatchBonus(@RequestParam(value = "orderId", required = true) Long orderId){
        orderService.dispatchBonus(orderId);
        return ok();
    }

    @PostMapping("dispatchBonusBatch")
    @ApiOperation("一键派奖多个单子")
    public R dispatchBonusBatch(@RequestBody List<Long> orderIds){
        orderService.dispatchBonusBatch(orderIds);
        return ok();
    }


}
