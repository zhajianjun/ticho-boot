package top.ticho.starter.s3.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import top.ticho.tool.s3.TiS3Property;
import top.ticho.tool.s3.TiS3Template;

/**
 * s3配置
 *
 * @author zhajianjun
 * @date 2025-09-06 15:14
 */
@Configuration
@ConditionalOnProperty(value = "ticho.s3.enable", havingValue = "true")
@PropertySource("classpath:DefaultSevletMultiartConfig.properties")
public class TiS3Config {

    @Bean
    @ConfigurationProperties(prefix = "ticho.s3")
    public TiS3Property tiS3Property() {
        return new TiS3Property();
    }

    @Bean
    public TiS3Template tiS3Template(TiS3Property tiS3Property) {
        return new TiS3Template(tiS3Property);
    }

}
