package com.caipiao.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午8:17
 */
@Data
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    private String appId;

    private String merchantPrivateKey;

    private String alipayPublicKey;

    private String notifyUrl;

    private String returnUrl; //支付成功跳转的页面

    private String signType;

    private String charset;

    private String gatewayUrl;

    private String quitUrl; // 用户退出跳转的页面

}

