package top.ticho.starter.minio.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import top.ticho.starter.minio.component.MinioTemplate;
import top.ticho.starter.minio.prop.MinioProperty;

/**
 * minio配置
 *
 * @author zhajianjun
 * @date 2022-09-13 15:54
 */
@Configuration
@ConditionalOnProperty(value = "ticho.minio.enable", havingValue = "true")
@PropertySource("classpath:DefaultSevletMultiartConfig.properties")
public class DefaultMinioConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.minio")
    public MinioProperty minioProperty() {
        return new MinioProperty();
    }

    @Bean
    public MinioTemplate minioTemplate(MinioProperty minioProperty) {
        return new MinioTemplate(minioProperty);
    }

}
