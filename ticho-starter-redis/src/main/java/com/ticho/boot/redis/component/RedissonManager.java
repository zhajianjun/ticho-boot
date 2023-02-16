package com.ticho.boot.redis.component;

import com.ticho.boot.redis.component.strategy.ClusterRedissonConfigStrategyImpl;
import com.ticho.boot.redis.component.strategy.MasterslaveRedissonConfigStrategyImpl;
import com.ticho.boot.redis.component.strategy.RedissonConfigContext;
import com.ticho.boot.redis.component.strategy.SentinelRedissonConfigStrategyImpl;
import com.ticho.boot.redis.component.strategy.StandaloneRedissonConfigStrategyImpl;
import com.ticho.boot.redis.enums.RedissonType;
import com.ticho.boot.redis.prop.BaseRedissonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.util.Assert;

/**
 * Redisson核心配置，用于提供初始化的redisson实例
 *
 * @author pangu
 * @date 2020-10-22
 */
@Slf4j
@Getter
public class RedissonManager {

    private final RedissonClient redisson;

    public RedissonManager(BaseRedissonProperty redissonProperties) {
        // @formatter:off
        try {
            Config config = RedissonConfigFactory.getInstance().createConfig(redissonProperties);
            redisson = Redisson.create(config);
        } catch (Exception e) {
            log.error("Redisson init error", e);
            throw new IllegalArgumentException("please input correct configurations,connectionType must in standalone/sentinel/cluster/masterslave");
        }
        // @formatter:on
    }

    /**
     * Redisson连接方式配置工厂
     * 双重检查锁
     */
    static class RedissonConfigFactory {

        private static volatile RedissonConfigFactory factory = null;

        public static RedissonConfigFactory getInstance() {
            if (factory == null) {
                synchronized (Object.class) {
                    if (factory == null) {
                        factory = new RedissonConfigFactory();
                    }
                }
            }
            return factory;
        }

        /**
         * 根据连接类型获取对应连接方式的配置,基于策略模式
         *
         * @param redissonProperties redisson配置
         * @return Config
         */
        Config createConfig(BaseRedissonProperty redissonProperties) {
            Assert.notNull(redissonProperties.getAddress(), "redisson.lock.server.address cannot be NULL!");
            RedissonType type = redissonProperties.getType();
            // 声明配置上下文
            RedissonConfigContext redissonConfigContext;
            if (type.compareTo(RedissonType.STANDALONE) == 0) {
                redissonConfigContext = new RedissonConfigContext(new StandaloneRedissonConfigStrategyImpl());
            } else if (type.compareTo(RedissonType.SENTINEL) == 0) {
                redissonConfigContext = new RedissonConfigContext(new SentinelRedissonConfigStrategyImpl());
            } else if (type.compareTo(RedissonType.CLUSTER) == 0) {
                redissonConfigContext = new RedissonConfigContext(new ClusterRedissonConfigStrategyImpl());
            } else if (type.compareTo(RedissonType.MASTERSLAVE) == 0) {
                redissonConfigContext = new RedissonConfigContext(new MasterslaveRedissonConfigStrategyImpl());
            } else {
                throw new IllegalArgumentException("创建Redisson连接Config失败！当前连接方式:" + type);
            }
            return redissonConfigContext.createRedissonConfig(redissonProperties);
        }
    }


}
