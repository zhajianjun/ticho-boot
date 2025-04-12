package top.ticho.starter.es.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ticho.starter.es.component.EsTemplate;
import top.ticho.starter.es.component.EsTemplateImpl;

/**
 * es bean 配置
 *
 * @author zhajianjun
 * @date 2023-04-17 13:59
 */
@Configuration
public class TiEsBeanConfig {

    @Bean
    public EsTemplate esTemplate(RestHighLevelClient restHighLevelClient) {
        return new EsTemplateImpl(restHighLevelClient);
    }

}
