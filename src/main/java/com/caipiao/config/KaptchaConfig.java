package com.caipiao.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.code.kaptcha.util.Config;

import java.util.Properties;

/**
 * jzcloud
 * 图形验证码配置
 * @author : xiaoyinandan
 * @date : 2021/4/19 13:15
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.font.color", "black");
        properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        properties.put("kaptcha.textproducer.char.space", "7");
        properties.put("kaptcha.textproducer.char.length", "4");
        properties.put("kaptcha.textproducer.char.string", "2345678");
        properties.put("kaptcha.textproducer.font.names", "Arial,Courier,cmr10,宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
