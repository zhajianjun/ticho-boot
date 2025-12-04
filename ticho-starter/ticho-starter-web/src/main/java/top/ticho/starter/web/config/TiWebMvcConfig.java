package top.ticho.starter.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.starter.web.advice.TiResponseBodyAdvice;
import top.ticho.starter.web.factory.TiYamlPropertySourceFactory;

import jakarta.annotation.Resource;

/**
 * 基础视图配置
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 10)
@PropertySource(factory = TiYamlPropertySourceFactory.class, value = "classpath:ticho-web.yaml")
public class TiWebMvcConfig implements WebMvcConfigurer {

    /**
     * Jackson2ObjectMapperBuilderCustomizer的自定义bean的配置会体现在生成的MappingJackson2HttpMessageConverter的bean中
     * <p>
     * // * @see TiJacksonCustomizerConfig#jackson2ObjectMapperBuilderCustomizer()
     */
    @Resource
    private JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter;

    /**
     * 主要处理  ResponseHandle#beforeBodyWrite中返回String，匹配的是StringHttpMessageConverter，但是最终返回的是Result对象，导致
     * StringHttpMessageConverter转换异常，所以添加JacksonJsonHttpMessageConverter作为默认的转换器
     *
     * @see TiResponseBodyAdvice#beforeBodyWrite(Object, org.springframework.core.MethodParameter, org.springframework.http.MediaType, Class, org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse)(Object, org.springframework.core.MethodParameter, org.springframework.http.MediaType, Class, org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse)
     */
    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        builder.addCustomConverter(jacksonJsonHttpMessageConverter);
    }

}
