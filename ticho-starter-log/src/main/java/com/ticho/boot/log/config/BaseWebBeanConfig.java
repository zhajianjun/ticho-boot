package com.ticho.boot.log.config;

import com.ticho.boot.log.filter.WapperRequestFilter;
import com.ticho.boot.log.interceptor.WebLogInterceptor;
import com.ticho.boot.log.prop.BaseLogProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * bean初始化配置
 *
 * @author zhajianjun
 * @date 2023-04-14 09:18
 */
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
@PropertySource(value = "classpath:ticho-log.properties")
public class BaseWebBeanConfig {

    @Bean
    @ConditionalOnMissingBean(WapperRequestFilter.class)
    public WapperRequestFilter wapperRequestFilter() {
        return new WapperRequestFilter();
    }

    @Bean
    @ConditionalOnBean(WapperRequestFilter.class)
    @ConditionalOnMissingBean(WebLogInterceptor.class)
    public WebLogInterceptor webLogInterceptor(BaseLogProperty baseLogProperty, Environment environment) {
        return new WebLogInterceptor(baseLogProperty, environment);
    }

}
