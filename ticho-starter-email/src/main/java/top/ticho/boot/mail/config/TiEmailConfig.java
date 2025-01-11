package top.ticho.boot.mail.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ticho.boot.mail.component.TiMailTemplate;
import top.ticho.boot.mail.prop.TiMailProperty;

/**
 * 默认email配置
 *
 * @author zhajianjun
 * @date 2024-02-06 10:12
 */
@Configuration
@ConditionalOnProperty(value = "ticho.mail.enable", havingValue = "true")
public class TiEmailConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.mail")
    public TiMailProperty mailProperty() {
        return new TiMailProperty();
    }

    @Bean
    public TiMailTemplate mailTemplate(TiMailProperty tiMailProperty) {
        return new TiMailTemplate(tiMailProperty);
    }


}
