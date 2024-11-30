package top.ticho.boot.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.ticho.boot.gateway.filter.ApiGlobalFilter;
import top.ticho.boot.view.log.BaseLogProperty;

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
    public BaseLogProperty baseLogProperty() {
        return new BaseLogProperty();
    }

    @Bean
    public ApiGlobalFilter apiGlobalFilter(BaseLogProperty baseLogProperty, Environment environment) {
        return new ApiGlobalFilter(baseLogProperty, environment);
    }

}
