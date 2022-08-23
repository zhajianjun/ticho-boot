package com.ticho.boot.web.config;

import com.ticho.boot.web.constant.WebConst;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义异步线程池
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Configuration
@ConfigurationProperties(prefix = WebConst.ASYNC_PREFIX)
@Data
@Slf4j
@EnableAsync
public class TichoAsyncConfig implements AsyncConfigurer {

    /**
     * 核心线程数，线程池维护线程的最小数量.
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 最大线程数，线程池维护线程的最大数量
     */
    private int maxPoolSize = corePoolSize * 2;
    /**
     * 队列容量
     */
    private int queueCapacity = 20;
    /**
     * 线程活跃时间（秒）
     */
    private int keepAliveSeconds = 60;
    /**
     * 设置线程名称
     */
    private String threadNamePrefix = "ticho";

    // @formatter:off

    /**
     * 获取异步线程池执行对象
     */
    @Override
    @Bean("asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        taskExecutor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        taskExecutor.setMaxPoolSize(maxPoolSize);
        // 设置队列容量
        taskExecutor.setQueueCapacity(queueCapacity);
        // 设置线程活跃时间（秒）
        taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        // 设置线程名称
        taskExecutor.setThreadNamePrefix(threadNamePrefix);
        // 设置拒绝策略
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
    * 异步线程执行失败异常处理
     *
    * @return AsyncUncaughtExceptionHandler
    */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> {
            String errorMessage = "Async execution error on method:" + method.toString() + " with parameters:" + Arrays.toString(params);
            log.error(errorMessage);
        };
    }
}
