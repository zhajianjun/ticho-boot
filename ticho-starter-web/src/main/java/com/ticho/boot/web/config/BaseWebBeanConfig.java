package com.ticho.boot.web.config;

import com.ticho.boot.view.task.BaseTaskDecortor;
import com.ticho.boot.web.prop.BaseAsyncProperty;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhajianjun
 * @date 2023-01-05 13:08
 */
@Configuration
public class BaseWebBeanConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.async")
    public BaseAsyncProperty baseAsyncProperty() {
        return new BaseAsyncProperty();
    }

    @Bean("asyncTaskExecutor")
    public Executor executor(BaseAsyncProperty property, TaskDecorator taskDecorator) {
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
    public BaseTaskDecortor<RequestAttributes> requestAttributes() {
        BaseTaskDecortor<RequestAttributes> decortor = new BaseTaskDecortor<>();
        decortor.setSupplier(RequestContextHolder::currentRequestAttributes);
        decortor.setExecute(RequestContextHolder::setRequestAttributes);
        decortor.setComplete(x -> RequestContextHolder.resetRequestAttributes());
        return decortor;
    }

    @Bean
    public BaseTaskDecortor<Map<String, String>> mdc() {
        BaseTaskDecortor<Map<String, String>> decortor = new BaseTaskDecortor<>();
        decortor.setSupplier(MDC::getCopyOfContextMap);
        decortor.setExecute(MDC::setContextMap);
        decortor.setComplete(x -> MDC.clear());
        return decortor;
    }

    @Bean
    public TaskDecorator taskDecorator(List<BaseTaskDecortor<?>> taskDecorators) {
        return runnable -> {
            try {
                taskDecorators.forEach(BaseTaskDecortor::setData);
                return () -> {
                    try {
                        taskDecorators.forEach(BaseTaskDecortor::execute);
                        runnable.run();
                    } finally {
                        taskDecorators.forEach(BaseTaskDecortor::complete);
                    }
                };
            } catch (IllegalStateException e) {
                return runnable;
            }
        };
    }

}
