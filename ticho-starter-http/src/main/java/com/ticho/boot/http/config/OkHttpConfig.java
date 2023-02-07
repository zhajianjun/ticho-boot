package com.ticho.boot.http.config;

import com.ticho.boot.http.interceptor.OkHttpLogInterceptor;
import com.ticho.boot.http.prop.BaseHttpProperty;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RestTemplate配置
 *
 * @author zhajianjun
 * @date 2021-10-28 23:43
 */
@Configuration
@Slf4j
@PropertySource(value = "classpath:ticho-http.properties")
public class OkHttpConfig {

    /**
     * 用于修改okhttp
     */
    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConfigurationProperties(prefix = "ticho.http")
    public BaseHttpProperty tichoFeignProperty() {
        return new BaseHttpProperty();
    }

    @Bean
    @ConditionalOnMissingBean(Interceptor.class)
    public Interceptor defaultInterceptor() {
        return chain -> {
            Request req = chain.request();
            return chain.proceed(req);
        };
    }

    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean({OkHttpClient.class})
    public OkHttpClient okHttpClient(BaseHttpProperty prop, List<Interceptor> interceptors) {
        // @formatter:off
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        interceptors.forEach(builder::addInterceptor);
        if (Boolean.TRUE.equals(prop.getOpenLog())) {
            builder.addInterceptor(new OkHttpLogInterceptor(prop));
        }
        return builder
            // 设置连接超时
            .connectTimeout(prop.getConnectTimeout() , TimeUnit.SECONDS)
            // 设置读超时
            .readTimeout(prop.getConnectTimeout() , TimeUnit.SECONDS)
            // 设置写超时
            .writeTimeout(prop.getWriteTimeout() , TimeUnit.SECONDS)
            // 是否自动重连
            .retryOnConnectionFailure(prop.getRetryOnConnectionFailure())
            .connectionPool(new ConnectionPool(prop.getMaxIdleConnections() , prop.getKeepAliveDuration(), TimeUnit.MINUTES))
            .build();
        // @formatter:on
    }

    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean({OkHttp3ClientHttpRequestFactory.class})
    public OkHttp3ClientHttpRequestFactory httpRequestFactory(OkHttpClient okHttpClient) {
        return new OkHttp3ClientHttpRequestFactory(okHttpClient);
    }

    /**
     * 基于OkHttp3配置RestTemplate
     */
    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean({RestTemplate.class})
    public RestTemplate restTemplate(OkHttp3ClientHttpRequestFactory httpRequestFactory) {
        return new RestTemplate(httpRequestFactory);
    }

}
