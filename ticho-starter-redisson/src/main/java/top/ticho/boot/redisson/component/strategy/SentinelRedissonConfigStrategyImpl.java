package top.ticho.boot.redisson.component.strategy;

import cn.hutool.core.util.StrUtil;
import top.ticho.boot.redisson.constant.BaseRedissonConst;
import top.ticho.boot.redisson.prop.BaseRedissonProperty;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;

/**
 * 哨兵方式Redis连接配置
 *
 * @author zhajianjun
 * @date 2020-10-22
 */
@Slf4j
public class SentinelRedissonConfigStrategyImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(BaseRedissonProperty redissonProperties) {
        Config config = new Config();
        try {
            String address = redissonProperties.getAddress();
            String password = redissonProperties.getPassword();
            int database = redissonProperties.getDatabase();
            String[] addrTokens = address.split(",");
            String sentinelAliasName = addrTokens[0];
            // 设置redis配置文件sentinel.conf配置的sentinel别名
            config.useSentinelServers().setMasterName(sentinelAliasName);
            config.useSentinelServers().setDatabase(database);
            if (StrUtil.isNotBlank(password)) {
                config.useSentinelServers().setPassword(password);
            }
            // 设置sentinel节点的服务IP和端口
            for (int i = 1; i < addrTokens.length; i++) {
                config.useSentinelServers().addSentinelAddress(BaseRedissonConst.prefix + addrTokens[i]);
            }
            log.info("初始化[sentinel]方式Config,redisAddress:" + address);
        } catch (Exception e) {
            log.error("sentinel Redisson init error", e);
            e.printStackTrace();
        }
        return config;
    }
}
