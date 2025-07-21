package top.ticho.trace.spring.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.trace.common.TiTraceReporter;
import top.ticho.trace.common.TiTraceProperty;
import top.ticho.trace.spring.interceptor.TiTraceInterceptor;

/**
 * 链路bean初始化配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceBeanConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.trace")
    public TiTraceProperty traceLogProperty() {
        return new TiTraceProperty();
    }

    @Bean
    public TiTraceInterceptor traceInterceptor(TiTraceProperty tiTraceProperty, Environment environment, ObjectProvider<TiTraceReporter> tiReporterObjectProvider) {
        return new TiTraceInterceptor(tiTraceProperty, environment, tiReporterObjectProvider.getIfAvailable());
    }

}
