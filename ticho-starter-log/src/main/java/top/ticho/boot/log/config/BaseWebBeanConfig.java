package top.ticho.boot.log.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.boot.log.filter.WapperRequestFilter;
import top.ticho.boot.log.interceptor.WebLogInterceptor;
import top.ticho.boot.view.log.BaseLogProperty;
import top.ticho.boot.view.task.BaseTaskDecortor;
import top.ticho.tool.trace.core.util.TraceUtil;

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
    public BaseLogProperty baseLogProperty() {
        return new BaseLogProperty();
    }

    @Bean
    @ConditionalOnBean(WapperRequestFilter.class)
    public WebLogInterceptor webLogInterceptor(BaseLogProperty baseLogProperty, Environment environment) {
        return new WebLogInterceptor(baseLogProperty, environment);
    }

    /**
     * 下个跨度id的索引 上下文传递
     * <p>虽然TransmittableThreadLocal可以进行父子线程的变量传递，但是子线程结束也得清除线程变量，所以还是手动方式注入，子线程结束进行清除</p>
     *
     * @return {@link BaseTaskDecortor}<{@link AtomicInteger}>
     */
    @Bean
    public BaseTaskDecortor<AtomicInteger> nextSpanIndex() {
        BaseTaskDecortor<AtomicInteger> decortor = new BaseTaskDecortor<>();
        decortor.setSupplier(TraceUtil::getNextSpanIndex);
        decortor.setExecute(TraceUtil::setNextSpanIndex);
        decortor.setComplete(x -> TraceUtil.clearNextSpanIndex());
        return decortor;
    }

}
