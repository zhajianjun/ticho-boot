package top.ticho.starter.http.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import top.ticho.starter.http.prop.TiHttpProperty;

/**
 * RestTemplate配置
 *
 * @author zhajianjun
 * @date 2021-10-28 23:43
 */
@Configuration
@Slf4j
@PropertySource(value = "classpath:ticho-http.properties")
public class TiOkHttpConfig {

    /**
     * 用于修改okhttp
     */
    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConfigurationProperties(prefix = "ticho.http")
    public TiHttpProperty tichoHttpProperty() {
        return new TiHttpProperty();
    }

}
