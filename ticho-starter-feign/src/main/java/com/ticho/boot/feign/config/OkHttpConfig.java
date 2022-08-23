package com.ticho.boot.feign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置
 *
 * @author AdoroTutto
 * @date 2021-10-28 23:43
 */
@Configuration
public class OkHttpConfig {
    /**
     * 基于OkHttp3配置RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
