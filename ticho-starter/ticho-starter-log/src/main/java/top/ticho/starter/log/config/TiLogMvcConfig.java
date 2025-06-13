package top.ticho.starter.log.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.starter.log.interceptor.TiWebLogInterceptor;

import jakarta.annotation.Resource;

/**
 * 日志MVC配置
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
public class TiLogMvcConfig implements WebMvcConfigurer {

    @Resource
    private TiWebLogInterceptor tiWebLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tiWebLogInterceptor).order(tiWebLogInterceptor.getOrder());
    }

}
