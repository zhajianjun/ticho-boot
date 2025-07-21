package top.ticho.starter.log.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.ticho.starter.log.filter.TiWapperRequestFilter;
import top.ticho.starter.log.interceptor.TiWebLogInterceptor;
import top.ticho.starter.view.log.TiLogProperty;
import top.ticho.starter.view.task.TiTaskDecortor;
import top.ticho.trace.common.TiTraceReporter;
import top.ticho.trace.common.TiSpan;
import top.ticho.trace.common.TiTraceContext;
import top.ticho.trace.common.TiTracer;

import java.util.List;

/**
 * 日志bean初始化配置
 *
 * @author zhajianjun
 * @date 2023-04-14 09:18
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
public class TiWebBeanConfig {

    @Bean
    public TiWapperRequestFilter wapperRequestFilter() {
        return new TiWapperRequestFilter();
    }

    @Bean
    @ConfigurationProperties(prefix = "ticho.log")
    public TiLogProperty baseLogProperty() {
        return new TiLogProperty();
    }

    @Bean
    @ConditionalOnBean(TiWapperRequestFilter.class)
    public TiWebLogInterceptor webLogInterceptor(TiLogProperty tiLogProperty, Environment environment) {
        return new TiWebLogInterceptor(tiLogProperty, environment);
    }

    /**
     * 链路上下文传递
     */
    @Bean
    public TiTaskDecortor<TiTracer> tracerTaskDecortor(ObjectProvider<TiTraceReporter> tiReporterObjectProvider) {
        TiTaskDecortor<TiTracer> decortor = new TiTaskDecortor<>();
        decortor.setSupplier(TiTraceContext::getTiTracer);
        decortor.setExecute(item -> {
            if (item == null) {
                return;
            }
            TiTraceReporter ifAvailable = tiReporterObjectProvider.getIfAvailable();
            TiTraceReporter tiTraceReporter = new TiTraceReporter() {
                @Override
                public void report(TiSpan tiSpan) {

                }

                @Override
                public void reportBatch(List<TiSpan> tiSpans) {
                    if (ifAvailable == null) {
                        return;
                    }
                    if (tiSpans == null || tiSpans.isEmpty()) {
                        return;
                    }
                    tiSpans.remove(0);
                }
            };
            TiTraceContext.init(tiTraceReporter);
            TiTraceContext.start(item.rootSpan().copy());
        });
        decortor.setComplete(item -> {
            if (item == null) {
                return;
            }
            TiTraceContext.close();
        });
        return decortor;
    }

}
