package top.ticho.boot.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.ticho.boot.redis.util.RedisUtil;

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

}
