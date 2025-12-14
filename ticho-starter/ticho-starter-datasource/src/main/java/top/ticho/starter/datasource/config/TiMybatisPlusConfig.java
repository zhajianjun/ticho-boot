package top.ticho.starter.datasource.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import top.ticho.starter.datasource.injector.TiSqlInjector;

/**
 * mybatis plus config
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Configuration
@EnableTransactionManagement
@Order(Ordered.HIGHEST_PRECEDENCE)
@PropertySource(value = "classpath:ticho-mybatis-plus.properties")
public class TiMybatisPlusConfig {

    /**
     * Mybatis Plus 拦截器处理
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<InnerInterceptor> innerInterceptorsProvider) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        for (InnerInterceptor innerInterceptor : innerInterceptorsProvider) {
            interceptor.addInnerInterceptor(innerInterceptor);
        }
        return interceptor;
    }

    /**
     * 默认sql注入器
     */
    @Bean
    public TiSqlInjector tichoSqlInjector() {
        return new TiSqlInjector();
    }

}
