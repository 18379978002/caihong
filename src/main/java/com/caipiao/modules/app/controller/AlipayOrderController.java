package com.caipiao.modules.app.controller;

import cn.hutool.core.util.IdUtil;
import com.alipay.easysdk.factory.MultipleFactory;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/app/alipayorder")
@Api(tags = "《H5端》充值订单管理")
@Slf4j
public class AlipayOrderController {
    @Autowired
    private MultipleFactory factory;

    @PostMapping("submit")
    @RequiresAuthentication
    public R submit(@RequestParam(value = "amount") BigDecimal amount,
                    @RequestParam(value = "appName")String appName,
                    @RequestParam(value = "quitUrl")String quitUrl,
                    @RequestParam(value = "returnUrl")String returnUrl){

        try {
            AlipayTradeWapPayResponse pay = factory.Wap()
                    .pay("购买"+appName+"应用", IdUtil.createSnowflake(1,1).nextIdStr(), amount.toString(), quitUrl, returnUrl);
            return R.ok().put(pay);
        } catch (Exception e) {
            throw new RRException(e.getMessage());
        }
    }



}
