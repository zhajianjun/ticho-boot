package com.ticho.boot.redis.prop;

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
@ConfigurationProperties(prefix = "ticho.redis")
public class BaseRedisProperty {

    private String host;

    private Integer port;

    private String password;

    private int database = 0;


}
