package top.ticho.boot.es.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ticho.boot.es.component.EsTemplate;
import top.ticho.boot.es.component.EsTemplateImpl;

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
