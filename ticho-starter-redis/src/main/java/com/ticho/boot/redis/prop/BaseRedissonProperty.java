package com.ticho.boot.redis.prop;

import com.ticho.boot.redis.enums.RedissonType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-02-16 15:40
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ticho.redisson")
public class BaseRedissonProperty {

    private Boolean enable = false;

    private String address;

    private String password;

    private int database = 0;

    private RedissonType type = RedissonType.STANDALONE;

}
