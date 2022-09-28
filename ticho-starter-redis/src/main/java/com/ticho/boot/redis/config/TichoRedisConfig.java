package com.ticho.boot.redis.config;

import com.ticho.boot.redis.util.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis 配置
 * <p>默认使用stringRedisTemplate</p>
 *
 * @author zhajianjun
 * @date 2022-06-27 17:35
 */
@Configuration
@PropertySource(value = "classpath:ticho-redis.properties")
public class TichoRedisConfig {

    @Bean
    @ConditionalOnMissingBean(RedisUtil.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(RedisUtil.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public RedisUtil<String, String> redisUtil(StringRedisTemplate stringRedisTemplate) {
        return new RedisUtil<>(stringRedisTemplate);
    }

}
