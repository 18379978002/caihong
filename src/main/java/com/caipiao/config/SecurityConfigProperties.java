package com.caipiao.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "rainbow")
@Configuration
public class SecurityConfigProperties {
    private String encryptIv;
    private String encryptKey;
    private boolean enable;
}
