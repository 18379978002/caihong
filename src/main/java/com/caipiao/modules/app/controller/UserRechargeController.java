package com.caipiao.modules.app.controller;

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.easysdk.factory.MultipleFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.config.AlipayProperties;
import com.caipiao.modules.app.dto.UserRechargeDTO;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.app.service.UserRechargeRecordService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午8:23
 */
@RestController
@RequestMapping("/app/userrecharge")
@Api(tags = "《app端》用户充值管理")
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(AlipayProperties.class)
public class UserRechargeController extends AppAbstractController {

    private final WxPayService wxService;
    private final MultipleFactory factory;
    private final UserRechargeRecordService userRechargeRecordService;


    private final AlipayProperties alipayProperties;

    @PostMapping("createOrder")
    @ApiOperation("vip充值")
    public R createOrder(@RequestBody UserRechargeDTO dto) {
        ValidatorUtils.validateEntity(dto);
        UserRechargeRecord recharge = userRechargeRecordService.recharge(dto, getUser());
        return ok().put(recharge);

    }

    @GetMapping("{id}")
    @ApiOperation("查询订单")
    public R query(@PathVariable String id) {
        return ok().put(userRechargeRecordService.getById(id));

    }

    @GetMapping("list")
    @ApiOperation("资金明细条件分页查询")
    public R getList(@RequestParam Map<String, Object> params){
        IPage<UserRechargeRecord> page = new Query<UserRechargeRecord>().getPage(params);

        Object paymentType = params.get("paymentType");

        QueryWrapper<UserRechargeRecord> ne = new QueryWrapper<UserRechargeRecord>()
                .eq("user_id", getUser().getId())
                .eq(paymentType != null && StringUtils.isNotBlank((CharSequence) paymentType), "payment_type", paymentType);
        IPage<UserRechargeRecord> page1 = userRechargeRecordService.page(page, ne);

        return ok().put("page", new PageUtils<>(page1));
    }

    @PostMapping("/notify-order-wx")
    @AnonymousAccess
    public String wxPayNotifyOrder(@RequestBody String xmlData) {
        log.info("微信支付异步回调: {}", xmlData);
        try {
            WxPayOrderNotifyResult rs = wxService.parseOrderNotifyResult(xmlData);

            String returnCode = rs.getReturnCode();

            if (returnCode.equalsIgnoreCase("SUCCESS")) {
                if (rs.getResultCode().equalsIgnoreCase("SUCCESS")) {
                    userRechargeRecordService.payCallback(rs.getOutTradeNo(), rs.getTransactionId());
                }
            }

        } catch (WxPayException e) {
            return WxPayNotifyResponse.success(e.getReturnMsg());
        } catch (RRException e){
            return WxPayNotifyResponse.fail(e.getMsg());
        }
        return WxPayNotifyResponse.success("成功");
    }

    @PostMapping("/notify-order-ali")
    @AnonymousAccess
    public String aliPayNotifyOrder(HttpServletRequest request) {
        log.info("支付宝支付异步回调");
        try {
            Map<String,String> params = convertRequestParamsToMap(request);
            boolean verifyNotify = false;
            try {
                verifyNotify = AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(),"utf-8", "RSA2");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RRException("支付宝回调验签异常：" + e.getMessage(), 204);
            }

            if(!verifyNotify){
                throw new RRException("支付宝回调验签失败", 203);
            }

            if(params.get("trade_status").equals("TRADE_SUCCESS")){
                userRechargeRecordService.payCallback(params.get("out_trade_no"), params.get("trade_no"));
            }

        } catch (RRException e){
            log.info("支付宝回调异常: {}", e.getMsg());
            return e.getMsg();
        }catch (Exception e){
            log.info("支付宝回调异常: {}", e.getMessage());
            return e.getMessage();
        }
        return "success";
    }



    // 将request中的参数转换成Map
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
        // 将前台的参数转换为"xxx"->"aaa,bbb"的格式存入params中,实际上回调传来的参数每个key都只对应一个value
        for (Map.Entry<String, String[]> entry: requestParams.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            StringBuilder valStr = new StringBuilder();
            for (int i = 0; i < values.length; i ++) {
                if ( i != values.length - 1) {
                    valStr.append(values[i]).append(",");
                } else {
                    valStr.append(values[i]);
                }
            }
            params.put(key, valStr.toString());
        }
        return params;
    }

}
