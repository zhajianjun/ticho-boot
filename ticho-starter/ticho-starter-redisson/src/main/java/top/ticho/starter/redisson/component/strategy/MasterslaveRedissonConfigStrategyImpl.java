package top.ticho.starter.redisson.component.strategy;

import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import top.ticho.starter.redisson.constant.TiRedissonConst;
import top.ticho.starter.redisson.prop.TiRedissonProperty;
import top.ticho.tool.core.TiStrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主从方式Redisson配置
 * 连接方式：主节点,子节点,子节点
 * <p>格式为: 127.0.0.1:6379,127.0.0.1:6380,127.0.0.1:6381</p>
 */
@Slf4j
public class MasterslaveRedissonConfigStrategyImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(TiRedissonProperty redissonProperties) {
        Config config = new Config();
        try {
            String address = redissonProperties.getAddress();
            String password = redissonProperties.getPassword();
            int database = redissonProperties.getDatabase();
            String[] addrTokens = address.split(",");
            String masterNodeAddr = addrTokens[0];
            // 设置主节点ip
            config.useMasterSlaveServers().setMasterAddress(masterNodeAddr);
            if (TiStrUtil.isNotBlank(password)) {
                config.setPassword(password);
            }
            config.useMasterSlaveServers().setDatabase(database);
            // 设置从节点，移除第一个节点，默认第一个为主节点
            List<String> slaveList = new ArrayList<>();
            for (String addrToken : addrTokens) {
                slaveList.add(TiRedissonConst.prefix + addrToken);
            }
            slaveList.removeFirst();

            config.useMasterSlaveServers().addSlaveAddress(slaveList.toArray(new String[0]));
            log.info("初始化[MASTERSLAVE]方式Config,redisAddress:{}", address);
        } catch (Exception e) {
            log.error("MASTERSLAVE Redisson init error", e);
        }
        return config;
    }

}
