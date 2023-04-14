package com.ticho.boot.log.config;

import com.ticho.boot.log.interceptor.WebLogInterceptor;
import com.yomahub.tlog.springboot.lifecircle.TLogPropertyConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 日志MVC配置
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(WebLogInterceptor.class)
@AutoConfigureAfter(TLogPropertyConfiguration.class)
public class BaseLogMvcConfig implements WebMvcConfigurer {

    @Resource
    private WebLogInterceptor webLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webLogInterceptor).order(webLogInterceptor.getOrder());
    }

}
