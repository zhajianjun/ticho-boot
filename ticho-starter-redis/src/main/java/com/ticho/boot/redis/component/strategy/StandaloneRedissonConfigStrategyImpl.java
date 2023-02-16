package com.ticho.boot.redis.component.strategy;

import cn.hutool.core.util.StrUtil;
import com.ticho.boot.redis.constant.BaseRedisConst;
import com.ticho.boot.redis.prop.BaseRedissonProperty;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;

/**
 * 单机方式Redisson配置
 *
 * @author zhajianjun
 * @date 2020-10-22
 */
@Slf4j
public class StandaloneRedissonConfigStrategyImpl implements RedissonConfigStrategy {

	@Override
	public Config createRedissonConfig(BaseRedissonProperty redissonProperties) {
		Config config = new Config();
		try {
			String address = redissonProperties.getAddress();
			String password = redissonProperties.getPassword();
			int database = redissonProperties.getDatabase();
			String redisAddr = BaseRedisConst.prefix + address;
			config.useSingleServer().setAddress(redisAddr);
			config.useSingleServer().setDatabase(database);
			if (StrUtil.isNotBlank(password)) {
				config.useSingleServer().setPassword(password);
			}
			log.info("初始化[standalone]方式Config,redisAddress:" + address);
		} catch (Exception e) {
			log.error("standalone Redisson init error", e);
			e.printStackTrace();
		}
		return config;
	}
}
