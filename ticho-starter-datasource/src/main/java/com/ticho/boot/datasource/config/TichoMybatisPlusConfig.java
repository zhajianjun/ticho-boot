package com.ticho.boot.datasource.config;

import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.ticho.boot.datasource.injector.TichoSqlInjector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis plus config
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Configuration
@EnableTransactionManagement
@Order(Ordered.HIGHEST_PRECEDENCE)
@PropertySource(value = "classpath:ticho-mybatis-plus.properties")
public class TichoMybatisPlusConfig {

    /**
     * mybatis-plus 乐观锁拦截器
     */
    @Bean
    @ConditionalOnMissingBean(OptimisticLockerInnerInterceptor.class)
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    @Bean
    public TichoSqlInjector tichoSqlInjector() {
        return new TichoSqlInjector();
    }

}
