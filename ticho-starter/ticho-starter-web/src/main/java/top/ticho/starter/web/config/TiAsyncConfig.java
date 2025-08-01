package top.ticho.starter.web.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * 自定义异步线程池
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Data
@Slf4j
@ConditionalOnProperty(value = "ticho.async.enable", havingValue = "true")
@Configuration
public class TiAsyncConfig implements AsyncConfigurer {

    @Resource
    @Qualifier("asyncTaskExecutor")
    private Executor executor;

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
            String errorMessage = "Async execution error on method:" + method + " with parameters:" + Arrays.toString(params);
            log.error(errorMessage, ex);
        };
    }

}
