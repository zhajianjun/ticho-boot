package top.ticho.starter.redisson.component.strategy;

import org.redisson.config.Config;
import top.ticho.starter.redisson.prop.BaseRedissonProperty;

/**
 * Redisson配置构建接口
 *
 * @author zhajianjun
 * @date 2020-10-22
 */
public interface RedissonConfigStrategy {

    /**
     * 根据不同的Redis配置策略创建对应的Config
     *
     * @param redissonProperties redisson配置
     * @return Config
     */
    Config createRedissonConfig(BaseRedissonProperty redissonProperties);
}
