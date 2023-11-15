package com.caipiao.common.utils;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午9:13
 */
public interface AppConstant {

    /**
     * 充值redis订单key
     */
    String REDIS_RECHARGE_KEY_IS_PAY_0 = "mall:recharge:is_pay_0:";

    /**
     * 订单redis订单key
     */
    String REDIS_ORDER_KEY_IS_PAY_0 = "mall:order:is_pay_0:";

    /**
     * 订单自动取消时间（分钟）
     */
    long ORDER_TIME_OUT_0 = 30;
    long ORDER_TIME_OUT_1 = 1;

    /**
     * 微信支付校验类型
     */
    String WX_TRADE_TYPE_JSAPI = "JSAPI";
    String TRADE_TYPE_APP = "APP";
    String WX_TRADE_TYPE_NATIVE = "NATIVE";
    String RECHARGE_ORDER_NO_PREFIX = "RC";

    String ALI_TRADE_TYPE_APP = "APP";

    /**
     * 支付类型：1微信支付，2支付宝
     */
    String PAY_TYPE_1 = "1";

    /**
     * 支付类型：2支付宝
     */
    String PAY_TYPE_2 = "2";

    String ALI_RES_CODE_SUCCESS = "10000";

}

