package com.caipiao.config;

import com.alipay.easysdk.factory.MultipleFactory;
import com.alipay.easysdk.kernel.Config;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 支付宝支付Configuration
 * @author xiaoyinandan
 *
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(AlipayProperties.class)
public class AliPayConfiguration {

    private AlipayProperties properties;

    /**
     * 获取Factory
     * @return
     */
    @Bean
    public MultipleFactory factory() {
        MultipleFactory multipleFactory = new MultipleFactory();
        multipleFactory.setOptions(getOptions());
        return multipleFactory;
    }

    public Config getOptions() {

        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";
        config.signType = "RSA2";
        config.appId = properties.getAppId();

        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = properties.getMerchantPrivateKey();
        config.alipayPublicKey = properties.getAlipayPublicKey();
        config.notifyUrl = properties.getNotifyUrl();

        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
        //请填写您的应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt
//        config.merchantCertPath = properties.get();
//        //请填写您的支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt
//        config.alipayCertPath = payConfig.getPrivateKeyPath();
//        //请填写您的支付宝根证书文件路径，例如：/foo/alipayRootCert.crt
//        config.alipayRootCertPath = payConfig.getKeyPath();

        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        // config.alipayPublicKey = "<-- 请填写您的支付宝公钥，例如：MIIBIjANBg... -->";
        return config;
    }
}
