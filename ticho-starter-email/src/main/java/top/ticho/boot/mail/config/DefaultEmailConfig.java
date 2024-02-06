package top.ticho.boot.mail.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ticho.boot.mail.component.MailTemplate;
import top.ticho.boot.mail.prop.MailProperty;

/**
 * 默认email配置
 *
 * @author zhajianjun
 * @date 2024-02-06 10:12
 */
@Configuration
@ConditionalOnProperty(prefix = "ticho.mail", name = "host")
public class DefaultEmailConfig {

    @Bean
    public MailProperty mailProperty() {
        return new MailProperty();
    }

    @Bean
    public MailTemplate mailTemplate(MailProperty mailProperty) {
        return new MailTemplate(mailProperty);
    }


}
