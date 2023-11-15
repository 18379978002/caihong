package com.caipiao.modules.app.listener;

import cn.hutool.core.util.StrUtil;
import com.caipiao.common.utils.AppConstant;
import com.caipiao.common.utils.Constant;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.app.service.UserRechargeRecordService;
import com.caipiao.modules.order.dao.OrderDao;
import com.caipiao.modules.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyinandan
 * @date 2022/2/9 下午2:03
 */
@Component
@AllArgsConstructor
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    private RedisTemplate<String, Object> redisTemplate;

    private UserRechargeRecordService userRechargeRecordService;

    private OrderDao orderDao;


    @Override
    public void onMessage(Message message, byte[] bytes) {
        RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
        String channel = String.valueOf(serializer.deserialize(message.getChannel()));
        String body = String.valueOf(serializer.deserialize(message.getBody()));
        log.info("redis key失效，订单支付超时，订单ID：{}", body);
        //key过期监听
        if(StrUtil.format("__keyevent@{}__:expired", 1).equals(channel)){

            //充值订单自动取消
            if(body.contains(AppConstant.REDIS_RECHARGE_KEY_IS_PAY_0)) {
                body = body.replace(AppConstant.REDIS_RECHARGE_KEY_IS_PAY_0, "");
                String[] str = body.split(":");
                String orderId = str[1];

                UserRechargeRecord record = userRechargeRecordService.getById(orderId);

                if(null != record && Constant.PayStatus.NOT_PAY.getValue() == record.getPayStatus()){
                    record.setPayStatus(Constant.PayStatus.PAY_TIMEOUT.getValue());
                    userRechargeRecordService.updateById(record);

                    if(record.getSubject().contains("#")){
                        String planNo = record.getSubject().split("#")[1];
                        log.info("订单：{}，已超时，自动取消", planNo);
                        Order order = orderDao.selectById(planNo);
                        order.setPlanStatus(4);
                        orderDao.updateById(order);
                    }

                }


            }

            //彩票订单自动取消
            if(body.contains(AppConstant.REDIS_ORDER_KEY_IS_PAY_0)) {
                body = body.replace(AppConstant.REDIS_ORDER_KEY_IS_PAY_0, "");
                String[] str = body.split(":");
                String orderId = str[1];
                Order record = orderDao.selectById(Long.parseLong(orderId));
                if(null != record && Constant.PayStatus.NOT_PAY.getValue() == record.getPlanStatus()){
                    record.setPlanStatus(4);
                    orderDao.updateById(record);
                }
            }

        }
    }
}

