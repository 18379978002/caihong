package com.caipiao.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.datasource.hikari")//spring自带数据库连接池管理器
public class Db1HikariConfig extends HikariConfig {
}
