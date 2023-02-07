package com.ticho.boot.redis.config;

import com.ticho.boot.redis.util.RedisUtil;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
public class BaseRedisConfig {

    @Bean
    @ConditionalOnMissingBean(RedisUtil.class)
    public RedisUtil<String, String> redisUtil(StringRedisTemplate stringRedisTemplate) {
        return new RedisUtil<>(stringRedisTemplate);
    }

    @Bean(destroyMethod = "shutdown")
    public Redisson redisson(RedisProperties redisProperties) {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();
        singleServerConfig.setAddress("redis://" + host + ":" + port);
        singleServerConfig.setPassword(password);
        return (Redisson) Redisson.create(config);
    }

}
