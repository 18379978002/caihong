package com.caipiao;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@Slf4j
@MapperScan(basePackages = {"com.caipiao.modules.*.dao"})
public class BootApplication {

    public static void main(String[] args) {

        SpringApplication.run(BootApplication.class, args);
        log.info("++++++++++++++++++++项目启动完成++++++++++++++++++++");
    }

}
