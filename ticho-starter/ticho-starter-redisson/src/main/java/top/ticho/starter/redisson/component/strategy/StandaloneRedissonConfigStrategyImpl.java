package top.ticho.starter.redisson.component.strategy;

import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import top.ticho.starter.redisson.constant.TiRedissonConst;
import top.ticho.starter.redisson.prop.TiRedissonProperty;
import top.ticho.tool.core.TiStrUtil;

/**
 * 单机方式Redisson配置
 *
 * @author zhajianjun
 * @date 2020-10-22
 */
@Slf4j
public class StandaloneRedissonConfigStrategyImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(TiRedissonProperty redissonProperties) {
        Config config = new Config();
        try {
            String address = redissonProperties.getAddress();
            String password = redissonProperties.getPassword();
            int database = redissonProperties.getDatabase();
            String redisAddr = TiRedissonConst.prefix + address;
            config.useSingleServer().setAddress(redisAddr);
            config.useSingleServer().setDatabase(database);
            if (TiStrUtil.isNotBlank(password)) {
                config.setPassword(password);
            }
            log.info("初始化[standalone]方式Config,redisAddress:{}", address);
        } catch (Exception e) {
            log.error("standalone Redisson init error", e);
        }
        return config;
    }

}
