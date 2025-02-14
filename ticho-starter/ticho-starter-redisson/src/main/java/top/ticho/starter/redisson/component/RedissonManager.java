package top.ticho.starter.redisson.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.util.Assert;
import top.ticho.starter.redisson.component.strategy.ClusterRedissonConfigStrategyImpl;
import top.ticho.starter.redisson.component.strategy.MasterslaveRedissonConfigStrategyImpl;
import top.ticho.starter.redisson.component.strategy.RedissonConfigContext;
import top.ticho.starter.redisson.component.strategy.SentinelRedissonConfigStrategyImpl;
import top.ticho.starter.redisson.component.strategy.StandaloneRedissonConfigStrategyImpl;
import top.ticho.starter.redisson.enums.TiRedissonType;
import top.ticho.starter.redisson.prop.TiRedissonProperty;

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

    public RedissonManager(TiRedissonProperty redissonProperties) {
        try {
            Config config = RedissonConfigFactory.getInstance().createConfig(redissonProperties);
            redisson = Redisson.create(config);
        } catch (Exception e) {
            log.error("Redisson init error", e);
            throw new IllegalArgumentException("please input correct configurations,connectionType must in standalone/sentinel/cluster/masterslave");
        }
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
        Config createConfig(TiRedissonProperty redissonProperties) {
            Assert.notNull(redissonProperties.getAddress(), "redisson.lock.server.address cannot be NULL!");
            TiRedissonType type = redissonProperties.getType();
            // 声明配置上下文
            RedissonConfigContext redissonConfigContext;
            if (type.compareTo(TiRedissonType.STANDALONE) == 0) {
                redissonConfigContext = new RedissonConfigContext(new StandaloneRedissonConfigStrategyImpl());
            } else if (type.compareTo(TiRedissonType.SENTINEL) == 0) {
                redissonConfigContext = new RedissonConfigContext(new SentinelRedissonConfigStrategyImpl());
            } else if (type.compareTo(TiRedissonType.CLUSTER) == 0) {
                redissonConfigContext = new RedissonConfigContext(new ClusterRedissonConfigStrategyImpl());
            } else if (type.compareTo(TiRedissonType.MASTERSLAVE) == 0) {
                redissonConfigContext = new RedissonConfigContext(new MasterslaveRedissonConfigStrategyImpl());
            } else {
                throw new IllegalArgumentException("创建Redisson连接Config失败！当前连接方式:" + type);
            }
            return redissonConfigContext.createRedissonConfig(redissonProperties);
        }
    }


}
