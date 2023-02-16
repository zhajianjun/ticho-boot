package com.ticho.boot.web.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * 自定义异步线程池
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@ConditionalOnProperty(value = "ticho.async.enableAsync", havingValue = "true")
@Configuration
@Data
@Slf4j
public class BaseAsyncConfig implements AsyncConfigurer {

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private Executor executor;
    // @formatter:off

    /**
     * 获取异步线程池执行对象
     */
    @Override
    public Executor getAsyncExecutor() {
        return executor;
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
            log.error(errorMessage, ex);
        };
    }

}
