package top.ticho.boot.dict.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import top.ticho.boot.cache.component.TiCacheTemplate;
import top.ticho.boot.dict.component.DictRendererSerializer;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2024-12-28 15:07
 */
@Configuration
@ConditionalOnProperty(value = "ticho.dict.enable", havingValue = "true")
@PropertySource(value = "classpath:ticho-dict.properties")
public class TiDictConfig {

    @Bean
    public DictRendererSerializer dictRendererSerializer(TiCacheTemplate tiCacheTemplate, List<ObjectMapper> objectMappers) {
        return new DictRendererSerializer(tiCacheTemplate, objectMappers);
    }

}
