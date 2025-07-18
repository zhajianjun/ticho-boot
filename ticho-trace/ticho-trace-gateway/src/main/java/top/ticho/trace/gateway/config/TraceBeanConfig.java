package top.ticho.trace.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.ticho.trace.common.TiTraceProperty;
import top.ticho.trace.gateway.filter.TraceGlobalFilter;

/**
 * 链路bean初始化配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Configuration
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceBeanConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.trace")
    public TiTraceProperty traceLogProperty() {
        return new TiTraceProperty();
    }

    @Bean
    public TraceGlobalFilter traceGlobalFilter(TiTraceProperty tiTraceProperty, Environment environment) {
        return new TraceGlobalFilter(tiTraceProperty, environment);
    }

}
