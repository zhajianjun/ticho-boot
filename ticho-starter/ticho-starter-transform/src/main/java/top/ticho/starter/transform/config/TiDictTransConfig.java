package top.ticho.starter.transform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ticho.starter.transform.component.TiDictTransStrategy;
import top.ticho.starter.transform.constant.TiDictConst;
import top.ticho.starter.transform.factory.TiDictTransFactory;

/**
 * @author zhajianjun
 * @date 2025-03-24 22:21
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class TiDictTransConfig implements InitializingBean {

    private final TiDictTransStrategy tiDictTransStrategy;

    @Override
    public void afterPropertiesSet() {
        TiDictTransFactory.setDictTransStrategy(tiDictTransStrategy);
    }

    @Bean
    @ConditionalOnMissingBean(TiDictTransStrategy.class)
    public TiDictTransStrategy tiDictTransStrategy() {
        log.warn("{} strategy found for transform", TiDictConst.DEFAULT_DICT_NAME);
        return TiDictTransFactory.DEFAULT;
    }

}
