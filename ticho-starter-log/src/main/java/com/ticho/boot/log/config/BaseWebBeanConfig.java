package com.ticho.boot.log.config;

import com.ticho.boot.log.filter.WapperRequestFilter;
import com.ticho.boot.log.interceptor.WebLogInterceptor;
import com.ticho.boot.view.log.BaseLogProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 日志bean初始化配置
 *
 * @author zhajianjun
 * @date 2023-04-14 09:18
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
@PropertySource(value = "classpath:ticho-log.properties")
public class BaseWebBeanConfig {

    @Bean
    public WapperRequestFilter wapperRequestFilter() {
        return new WapperRequestFilter();
    }

    @Bean
    @ConfigurationProperties(prefix = "ticho.log")
    public BaseLogProperty baseLogProperty() {
        return new BaseLogProperty();
    }

    @Bean
    @ConditionalOnBean(WapperRequestFilter.class)
    public WebLogInterceptor webLogInterceptor(BaseLogProperty baseLogProperty, Environment environment) {
        return new WebLogInterceptor(baseLogProperty, environment);
    }

}