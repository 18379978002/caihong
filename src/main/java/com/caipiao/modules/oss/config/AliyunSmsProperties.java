package com.caipiao.modules.oss.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ego-maker
 *
 * @author : xiaoyinandan
 * @date : 2020/11/25 16:59
 */
@Data
@ToString
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliyunSmsProperties {
    private String regionId;
    private String domain;
    private String version;
    private String signName;
    private String accessKey;
    private String accessSecret;
}

