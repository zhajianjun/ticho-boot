package top.ticho.starter.transform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import top.ticho.starter.cache.component.TiCacheTemplate;
import top.ticho.starter.transform.factory.TiDictTransFactory;

import javax.annotation.Resource;

/**
 * @author zhajianjun
 * @date 2025-03-24 22:21
 */
@RequiredArgsConstructor
@Configuration
public class TiDictTransConfig implements InitializingBean {

    @Resource
    private TiCacheTemplate tiCacheTemplate;

    @Override
    public void afterPropertiesSet() {
        TiDictTransFactory.setCacheTemplate(tiCacheTemplate);
    }

}
