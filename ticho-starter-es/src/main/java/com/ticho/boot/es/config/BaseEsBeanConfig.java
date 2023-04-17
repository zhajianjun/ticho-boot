package com.ticho.boot.es.config;

import com.ticho.boot.es.component.EsTemplate;
import com.ticho.boot.es.component.EsTemplateImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * es bean 配置
 *
 * @author zhajianjun
 * @date 2023-04-17 13:59
 */
@Configuration
public class BaseEsBeanConfig {

    @Bean
    public EsTemplate esTemplate(RestHighLevelClient restHighLevelClient) {
        return new EsTemplateImpl(restHighLevelClient);
    }

}
