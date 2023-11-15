package com.caipiao.config;

import cn.hutool.core.util.StrUtil;
import com.caipiao.modules.app.listener.RedisKeyExpirationListener;
import com.caipiao.modules.app.service.UserRechargeRecordService;
import com.caipiao.modules.order.dao.OrderDao;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis监听器配置
 * redis.conf文件配置  notify-keyspace-events Ex
 */
@Configuration
@AllArgsConstructor
public class RedisListenerConfig {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRechargeRecordService userRechargeRecordService;
    private final OrderDao orderDao;

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new RedisKeyExpirationListener(redisTemplate, userRechargeRecordService, orderDao), new PatternTopic(StrUtil.format("__keyevent@{}__:expired", 1)));
        return container;
    }
}
