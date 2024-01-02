package top.ticho.boot.redisson.component.strategy;

import top.ticho.boot.redisson.prop.BaseRedissonProperty;
import lombok.AllArgsConstructor;
import org.redisson.config.Config;

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
    public Config createRedissonConfig(BaseRedissonProperty redissonProperties) {
        return this.redissonConfigStrategy.createRedissonConfig(redissonProperties);
    }
}
