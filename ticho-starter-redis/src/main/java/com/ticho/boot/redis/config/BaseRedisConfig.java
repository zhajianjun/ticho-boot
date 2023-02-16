package com.ticho.boot.redis.config;

import com.ticho.boot.redis.component.RedissonManager;
import com.ticho.boot.redis.prop.BaseRedissonProperty;
import com.ticho.boot.redis.util.RedisUtil;
import com.ticho.boot.redis.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.Executor;

/**
 * redis 配置
 * <p>默认使用stringRedisTemplate</p>
 *
 * @author zhajianjun
 * @date 2022-06-27 17:35
 */
@Configuration
@PropertySource(value = "classpath:ticho-redis.properties")
@Slf4j
public class BaseRedisConfig {

    @Bean
    @ConditionalOnMissingBean(RedisUtil.class)
    public RedisUtil<String, String> redisUtil(StringRedisTemplate stringRedisTemplate) {
        return new RedisUtil<>(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "ticho.redisson.enable", havingValue = "true")
    public RedissonManager redissonManager(BaseRedissonProperty property) {
        return new RedissonManager(property);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnBean(RedissonManager.class)
    public Redisson redisson(RedissonManager redissonManager) {
        return (Redisson) redissonManager.getRedisson();
    }

    @Bean
    @ConditionalOnBean(Redisson.class)
    public RedissonUtil redissonUtil(Redisson redisson, @Qualifier("asyncTaskExecutor") Executor executor) {
        return new RedissonUtil(redisson, executor);
    }

}
