package top.ticho.trace.spring.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.trace.spring.interceptor.TiTraceInterceptor;

import jakarta.annotation.Resource;

/**
 * 链路MVC配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.trace.enable", havingValue = "true", matchIfMissing = true)
public class TraceMvcConfig implements WebMvcConfigurer {

    @Resource
    private TiTraceInterceptor tiTraceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tiTraceInterceptor).order(tiTraceInterceptor.getOrder());
    }

}
