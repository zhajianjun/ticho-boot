package top.ticho.starter.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
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
     * mybatis-plus 乐观锁拦截器
     */
    @Bean
    @ConditionalOnMissingBean(OptimisticLockerInnerInterceptor.class)
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 分页拦截器
     */
    @Bean
    @ConditionalOnMissingBean(PaginationInnerInterceptor.class)
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }

    /**
     * 默认sql注入器
     */
    @Bean
    public TiSqlInjector tichoSqlInjector() {
        return new TiSqlInjector();
    }

}
