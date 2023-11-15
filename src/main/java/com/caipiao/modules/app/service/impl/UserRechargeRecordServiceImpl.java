package com.caipiao.modules.app.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alipay.easysdk.factory.MultipleFactory;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.AppConstant;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.common.utils.WebUtils;
import com.caipiao.modules.app.dao.UserRechargeRecordDao;
import com.caipiao.modules.app.dto.RechargeCountDTO;
import com.caipiao.modules.app.dto.UserRechargeDTO;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.entity.UserRechargeLevel;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.app.service.UserRechargeLevelService;
import com.caipiao.modules.app.service.UserRechargeRecordService;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import com.caipiao.modules.order.dao.BillingDao;
import com.caipiao.modules.order.dao.OrderDao;
import com.caipiao.modules.order.entity.Billing;
import com.caipiao.modules.order.entity.Order;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午3:17
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRechargeRecordServiceImpl extends ServiceImpl<UserRechargeRecordDao, UserRechargeRecord> implements UserRechargeRecordService {

    private final UserInfoService userInfoService;
    private final UserRechargeLevelService userRechargeLevelService;
    private final WxPayService wxService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MultipleFactory factory;
    private final OrderDao orderDao;
    private final UserRechargeRecordDao userRechargeRecordDao;
    private final BillingDao billingDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payCallback(String outTradeNo, String tradeNo) {
        UserRechargeRecord paymentOrder = this.getById(outTradeNo);
        //订单存在 , 且订单状态未支付, 且订单需要回调业务类
        if(null != paymentOrder &&
                Constant.PayStatus.NOT_PAY.getValue() == paymentOrder.getPayStatus() &&
                StrUtil.isNotBlank(paymentOrder.getId())){
            //更新充值记录以及用户的等级和过期时间

            paymentOrder.setTransactionId(tradeNo);
            paymentOrder.setPayStatus(Constant.PayStatus.PAY_SUCCESS.getValue());
            this.updateById(paymentOrder);
            UserInfo userInfo = userInfoService.getById(paymentOrder.getUserId());
            //检测是否是订单充值
            if(paymentOrder.getSubject().contains("#")){

                String planNo = paymentOrder.getSubject().split("#")[1];
                log.info("订单：{}，充值，更改订单状态为已支付", planNo);
                Order order = orderDao.selectById(Long.parseLong(planNo));
                order.setPlanStatus(1);
                orderDao.updateById(order);

                Billing billing = billingDao.selectById(order.getBillingId());
                boolean update = false;
                if(order.getIsBilling() == 1 && order.getBillingId() != null){
                    update = true;
                    billing.setPlanStatus(1);
                }
                //跟单，热度加1
                if(order.getIsDocumentary() == 1){
                    update = true;
                    billing.setBillingHeat(billing.getBillingHeat() + 1);
                }

                if(update){
                    billingDao.updateById(billing);
                }

                //加入消费记录
                UserRechargeRecord record = new UserRechargeRecord();
                record.setRechargeAmount(order.getAmount().negate());
                record.setSubject("消费");
                record.setPayStatus(1);
                //投注
                record.setPaymentType(2);
                record.setTransactionId(planNo);
                record.setUserId(order.getUserId());
                record.setCreateTime(new Date());
                record.setPaymentTime(new Date());
                userRechargeRecordDao.insert(record);

            }else{
                //更新用户余额信息
                userInfo.setBonus(userInfo.getBonus().add(paymentOrder.getRechargeAmount()));
                userInfo.setBalance(userInfo.getBalance().add(paymentOrder.getRechargeAmount()));
                userInfoService.updateById(userInfo);
            }
        }
    }

    @Override
    public UserRechargeRecord recharge(UserRechargeDTO dto, UserInfo user) {
        String rechargeLevelId = dto.getRechargeLevelId();

        UserRechargeLevel level = userRechargeLevelService.getById(rechargeLevelId);

        UserRechargeRecord record = new UserRechargeRecord();
        record.setPaymentTime(new Date());
        record.setPaymentWay(dto.getPaymentWay()+"");
        //充值
        record.setPaymentType(1);
        if(StringUtils.isNotBlank(dto.getPlanNo())){
            record.setSubject("用户充值并支付#" + dto.getPlanNo());
        }else{
            record.setSubject("用户充值");
        }

        record.setPayStatus(Constant.PayStatus.NOT_PAY.getValue());

        if(null == level){
            record.setRechargeAmount(dto.getRechargeMoney());
        }else{
            record.setRechargeAmount(level.getRechargeAmount());
        }

        record.setUserId(user.getId());
        this.save(record);

        //微信支付
        if (Constant.PaymentWay.WECHAT_PAY.getValue() == dto.getPaymentWay()) {
            WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
            wxPayUnifiedOrderRequest.setBody(record.getSubject());
            wxPayUnifiedOrderRequest.setOutTradeNo(record.getId());
            wxPayUnifiedOrderRequest.setTotalFee(record.getRechargeAmount().multiply(new BigDecimal(100)).intValue());
            wxPayUnifiedOrderRequest.setTradeType("APP");
            //wxPayUnifiedOrderRequest.setNotifyUrl("https://bus.liangxiangtech.cn/e-wallet/payment/notify-order-wx");
            wxPayUnifiedOrderRequest.setSpbillCreateIp(WebUtils.getIP());
            try {
                WxPayAppOrderResult wxPayAppOrderResult = wxService.createOrder(wxPayUnifiedOrderRequest);
                record.setWxPayAppOrder(wxPayAppOrderResult);
            } catch (WxPayException e) {
                throw new RRException(e.getMessage());
            }

            //加入redis，30分钟自动取消
            String keyRedis = String.valueOf(StrUtil.format("{}{}:{}", AppConstant.REDIS_RECHARGE_KEY_IS_PAY_0, "10000", record.getId()));
            redisTemplate.opsForValue().set(keyRedis, record.getId(), AppConstant.ORDER_TIME_OUT_0, TimeUnit.MINUTES);//设置过期时间
            return record;
        }

        //支付宝下单
        if (Constant.PaymentWay.ALIPAY.getValue() == dto.getPaymentWay()) {
            try {
                AlipayTradeWapPayResponse pay = factory.Wap()
                        //.asyncNotify("https://bus.liangxiangtech.cn/e-wallet/payment/notify-order-ali")
                        .pay(record.getSubject(), record.getId(), record.getRechargeAmount().toString(), dto.getQuitUrl(), dto.getReturnUrl());
                record.setAlipayTradeWapPayResponse(pay);
            } catch (Exception e) {
                throw new RRException(e.getMessage());
            }

            //加入redis，30分钟自动取消
            String keyRedis = String.valueOf(StrUtil.format("{}{}:{}", AppConstant.REDIS_RECHARGE_KEY_IS_PAY_0, "10000", record.getId()));
            redisTemplate.opsForValue().set(keyRedis, record.getId(), AppConstant.ORDER_TIME_OUT_0, TimeUnit.MINUTES);//设置过期时间
            return record;
        }

        throw new RRException("未知支付方式");
    }

    @Override
    public List<RechargeCountDTO> queryCountData(CommonLotteryDTO dto) {
        return baseMapper.queryCountData(dto);
    }


}
