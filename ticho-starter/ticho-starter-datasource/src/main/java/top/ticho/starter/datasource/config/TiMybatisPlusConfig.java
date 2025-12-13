package top.ticho.starter.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
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
     * 添加分页插件
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 1.创建MybatisPlusInterceptor拦截器对象
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页拦截器，如果配置多个插件, 切记分页最后添加
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁拦截器
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
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
