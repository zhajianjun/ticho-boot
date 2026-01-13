package top.ticho.starter.redisson.component.strategy;

import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import top.ticho.starter.redisson.constant.TiRedissonConst;
import top.ticho.starter.redisson.prop.TiRedissonProperty;
import top.ticho.tool.core.TiStrUtil;

/**
 * 集群方式Redisson配置
 *
 * @author zhajianjun
 * @date 2020-10-22
 */
@Slf4j
public class ClusterRedissonConfigStrategyImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(TiRedissonProperty redissonProperties) {
        Config config = new Config();
        try {
            String address = redissonProperties.getAddress();
            String password = redissonProperties.getPassword();
            String[] addrTokens = address.split(",");
            // 设置cluster节点的服务IP和端口
            for (String addrToken : addrTokens) {
                config.useClusterServers().addNodeAddress(TiRedissonConst.prefix + addrToken);
                if (TiStrUtil.isNotBlank(password)) {
                    config.setPassword(password);
                }
            }
            log.info("初始化[cluster]方式Config,redisAddress:{}", address);
        } catch (Exception e) {
            log.error("cluster Redisson init error", e);
        }
        return config;
    }

}
