package top.ticho.starter.log.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.starter.log.filter.WapperRequestFilter;
import top.ticho.starter.log.interceptor.WebLogInterceptor;
import top.ticho.starter.view.log.TiLogProperty;
import top.ticho.starter.view.task.TiTaskDecortor;
import top.ticho.trace.core.util.TraceUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 日志bean初始化配置
 *
 * @author zhajianjun
 * @date 2023-04-14 09:18
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
public class BaseWebBeanConfig {

    @Bean
    public WapperRequestFilter wapperRequestFilter() {
        return new WapperRequestFilter();
    }

    @Bean
    @ConfigurationProperties(prefix = "ticho.log")
    public TiLogProperty baseLogProperty() {
        return new TiLogProperty();
    }

    @Bean
    @ConditionalOnBean(WapperRequestFilter.class)
    public WebLogInterceptor webLogInterceptor(TiLogProperty tiLogProperty, Environment environment) {
        return new WebLogInterceptor(tiLogProperty, environment);
    }

    /**
     * 下个跨度id的索引 上下文传递
     * <p>虽然TransmittableThreadLocal可以进行父子线程的变量传递，但是子线程结束也得清除线程变量，所以还是手动方式注入，子线程结束进行清除</p>
     *
     * @return {@link TiTaskDecortor}<{@link AtomicInteger}>
     */
    @Bean
    public TiTaskDecortor<AtomicInteger> nextSpanIndex() {
        TiTaskDecortor<AtomicInteger> decortor = new TiTaskDecortor<>();
        decortor.setSupplier(TraceUtil::getNextSpanIndex);
        decortor.setExecute(TraceUtil::setNextSpanIndex);
        decortor.setComplete(x -> TraceUtil.clearNextSpanIndex());
        return decortor;
    }

}
