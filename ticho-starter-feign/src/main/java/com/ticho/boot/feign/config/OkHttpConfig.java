package com.ticho.boot.feign.config;

import feign.Logger;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * RestTemplate配置
 *
 * @author zhajianjun
 * @date 2021-10-28 23:43
 */
@Configuration
public class OkHttpConfig {
    /**
     * 基于OkHttp3配置RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(OkHttp3ClientHttpRequestFactory httpRequestFactory) {
        return new RestTemplate(httpRequestFactory);
    }

    @Bean
    public Logger.Level log() {
        return Logger.Level.FULL;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        // @formatter:off
        return new OkHttpClient().newBuilder()
            //设置连接超时
            .connectTimeout(10 , TimeUnit.SECONDS)
            //设置读超时
            .readTimeout(10 , TimeUnit.SECONDS)
            //设置写超时
            .writeTimeout(10 , TimeUnit.SECONDS)
            //是否自动重连
            .retryOnConnectionFailure(true)
            .connectionPool(new ConnectionPool(10 , 5L, TimeUnit.MINUTES))
            .build();
        // @formatter:on
    }

    @Bean
    public OkHttp3ClientHttpRequestFactory httpRequestFactory(OkHttpClient okHttpClient) {
        return new OkHttp3ClientHttpRequestFactory(okHttpClient);
    }

}
