package top.ticho.starter.redisson.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ticho.starter.redisson.component.RedissonManager;
import top.ticho.starter.redisson.prop.TiRedissonProperty;

/**
 * redis 配置
 * <p>默认使用stringTiResultmplate</p>
 *
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
@Configuration
@Slf4j
public class TiRedissonConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "ticho.redisson.enable", havingValue = "true")
    public RedissonManager redissonManager(TiRedissonProperty property) {
        return new RedissonManager(property);
    }

    // @Bean(destroyMethod = "shutdown")
    // @ConditionalOnBean(RedissonManager.class)
    // public Redisson redisson(RedissonManager redissonManager) {
    //     return (Redisson) redissonManager.getRedisson();
    // }
    //
    // @Bean
    // @ConditionalOnBean(value = {Redisson.class}, name = {"asyncTaskExecutor"})
    // public TiRedissonTemplate redissonUtil(Redisson redisson, @Qualifier("asyncTaskExecutor") Executor executor) {
    //     return new TiRedissonTemplate(redisson, executor);
    // }

}
