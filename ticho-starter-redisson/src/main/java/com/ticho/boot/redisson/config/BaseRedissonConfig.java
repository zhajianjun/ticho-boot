package com.ticho.boot.redisson.config;

import com.ticho.boot.redisson.component.RedissonManager;
import com.ticho.boot.redisson.prop.BaseRedissonProperty;
import com.ticho.boot.redisson.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

/**
 * redis 配置
 * <p>默认使用stringRedisTemplate</p>
 *
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
@Configuration
@Slf4j
public class BaseRedissonConfig {

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
    @ConditionalOnBean(value = {Redisson.class}, name = {"asyncTaskExecutor"})
    public RedissonUtil redissonUtil(Redisson redisson, @Qualifier("asyncTaskExecutor") Executor executor) {
        return new RedissonUtil(redisson, executor);
    }

}
