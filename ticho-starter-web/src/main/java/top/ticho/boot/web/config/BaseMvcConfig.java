package top.ticho.boot.web.config;

import top.ticho.boot.web.factory.YamlPropertySourceFactory;
import top.ticho.boot.web.handle.BaseBaseResponseHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 基础视图配置
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 10)
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:ticho-web.yaml")
public class BaseMvcConfig implements WebMvcConfigurer {

    /**
     * Jackson2ObjectMapperBuilderCustomizer的自定义bean的配置会体现在生成的MappingJackson2HttpMessageConverter的bean中
     * @see BaseJacksonCustomizerConfig#jackson2ObjectMapperBuilderCustomizer()
     */
    @Autowired(required = false)
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    /**
     * 主要处理  ResponseHandle#beforeBodyWrite中返回String,使用 StringHttpMessageConverter，但是最终返回的是Result对象，导致
     * StringHttpMessageConverter转换异常，所以去除StringHttpMessageConverter，并添加MappingJackson2HttpMessageConverter作为默认的转换器
     * @see BaseBaseResponseHandle#beforeBodyWrite(Object, org.springframework.core.MethodParameter, org.springframework.http.MediaType, Class, org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse)(Object, org.springframework.core.MethodParameter, org.springframework.http.MediaType, Class, org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse)
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, mappingJackson2HttpMessageConverter);
        converters.removeIf(httpMessageConverter -> httpMessageConverter.getClass() == StringHttpMessageConverter.class);
    }
}
