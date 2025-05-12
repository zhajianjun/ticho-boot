package top.ticho.starter.datasource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import top.ticho.starter.datasource.prop.TiDataSourceProperty;

/**
 * 数据源配置
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Configuration
@PropertySource(value = "classpath:ticho-datasource.properties")
public class TiDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.datasource")
    public TiDataSourceProperty tiDataSourceProperty() {
        return new TiDataSourceProperty();
    }

}
