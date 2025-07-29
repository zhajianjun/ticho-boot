package top.ticho.starter.web.config;

import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.ticho.starter.view.task.TiTaskDecortor;
import top.ticho.starter.web.prop.TiAsyncProperty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhajianjun
 * @date 2023-01-05 13:08
 */
@Configuration
public class TiWebBeanConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.async")
    public TiAsyncProperty baseAsyncProperty() {
        return new TiAsyncProperty();
    }

    @Bean("asyncTaskExecutor")
    public Executor executor(TiAsyncProperty property, TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        taskExecutor.setCorePoolSize(property.getCorePoolSize());
        // 设置最大线程数
        taskExecutor.setMaxPoolSize(property.getMaxPoolSize());
        // 设置队列容量
        taskExecutor.setQueueCapacity(property.getQueueCapacity());
        // 设置线程活跃时间（秒）
        taskExecutor.setKeepAliveSeconds(property.getKeepAliveSeconds());
        // 设置线程名称前缀
        taskExecutor.setThreadNamePrefix(property.getThreadNamePrefix());
        // 处理子线程与主线程间数据传递
        taskExecutor.setTaskDecorator(taskDecorator);
        // 设置拒绝策略
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }


    @Bean
    public TiTaskDecortor<RequestAttributes> requestAttributes() {
        TiTaskDecortor<RequestAttributes> decortor = new TiTaskDecortor<>();
        decortor.setSupplier(RequestContextHolder::currentRequestAttributes);
        decortor.setExecute(RequestContextHolder::setRequestAttributes);
        decortor.setComplete(x -> RequestContextHolder.resetRequestAttributes());
        return decortor;
    }

    @Bean
    public TiTaskDecortor<Map<String, String>> mdc() {
        TiTaskDecortor<Map<String, String>> decortor = new TiTaskDecortor<>();
        decortor.setSupplier(MDC::getCopyOfContextMap);
        decortor.setExecute(MDC::setContextMap);
        decortor.setComplete(x -> MDC.clear());
        return decortor;
    }

    @Bean
    public TaskDecorator taskDecorator(List<TiTaskDecortor<?>> taskDecorators) {
        return runnable -> {
            try {
                taskDecorators.forEach(TiTaskDecortor::initData);
                return () -> {
                    try {
                        taskDecorators.forEach(TiTaskDecortor::execute);
                        runnable.run();
                    } finally {
                        taskDecorators.forEach(TiTaskDecortor::complete);
                    }
                };
            } catch (IllegalStateException e) {
                return runnable;
            }
        };
    }

}
