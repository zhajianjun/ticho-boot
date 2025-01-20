package top.ticho.starter.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.ticho.starter.gateway.filter.ApiGlobalFilter;
import top.ticho.starter.view.log.TiLogProperty;

/**
 * bean初始化配置
 *
 * @author zhajianjun
 * @date 2023-04-14 15:47
 */
@Configuration
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
public class BeanConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.log")
    public TiLogProperty tiLogProperty() {
        return new TiLogProperty();
    }

    @Bean
    public ApiGlobalFilter apiGlobalFilter(TiLogProperty tiLogProperty, Environment environment) {
        return new ApiGlobalFilter(tiLogProperty, environment);
    }

}
