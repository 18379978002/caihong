package com.caipiao.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import com.caipiao.modules.order.dto.AmountCountDTO;
import com.caipiao.modules.order.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {
    String submitOrder(Order order, UserInfo user);

    /**
     * 支付
     * @param planNo
     * @param payType
     * @param user
     * @return
     */
    R pay(String planNo, Integer payType, UserInfo user, String payPassword);

    /**
     * 撤单
     * @param planNo
     * @param reason
     */
    void cancelOrder(String planNo, String reason);

    /**
     * 派奖
     * @param orderId
     */
    void dispatchBonus(Long orderId);

    void dispatchBonusBatch(List<Long> orderIds);

    List<AmountCountDTO> queryAmountCount(CommonLotteryDTO req);
}
