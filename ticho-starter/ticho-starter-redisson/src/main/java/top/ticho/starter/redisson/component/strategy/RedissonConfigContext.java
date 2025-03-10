package top.ticho.starter.redisson.component.strategy;

import lombok.AllArgsConstructor;
import org.redisson.config.Config;
import top.ticho.starter.redisson.prop.TiRedissonProperty;

/**
 * Redisson配置上下文，产出真正的Redisson的Config
 *
 * @author zhajianjun
 * @date 2020-10-22
 */
@AllArgsConstructor
public class RedissonConfigContext {

    private final RedissonConfigStrategy redissonConfigStrategy;

    /**
     * 上下文根据构造中传入的具体策略产出真实的Redisson的Config
     *
     * @param redissonProperties redisson配置
     * @return Config
     */
    public Config createRedissonConfig(TiRedissonProperty redissonProperties) {
        return this.redissonConfigStrategy.createRedissonConfig(redissonProperties);
    }
}
