package top.ticho.starter.redisson.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.ticho.starter.redisson.enums.TiRedissonType;

/**
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ticho.redisson")
public class TiRedissonProperty {

    private Boolean enable = false;

    private String address;

    private String password;

    private int database = 0;

    private TiRedissonType type = TiRedissonType.STANDALONE;

}
