package com.ticho.boot.log.config;

import com.ticho.boot.log.filter.WapperRequestFilter;
import com.ticho.boot.log.interceptor.WebLogInterceptor;
import com.ticho.boot.log.prop.TichoLogProperty;
import com.yomahub.tlog.springboot.lifecircle.TLogPropertyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Configuration
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true")
@PropertySource(value = "classpath:ticho-log.properties")
@AutoConfigureAfter(TLogPropertyConfiguration.class)
public class LogConfig implements WebMvcConfigurer {

    @Autowired
    private TichoLogProperty tichoLogProperty;

    @Bean
    public WapperRequestFilter wapperRequestFilter() {
        return new WapperRequestFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebLogInterceptor(tichoLogProperty)).order(Ordered.HIGHEST_PRECEDENCE + 10);
    }

}
