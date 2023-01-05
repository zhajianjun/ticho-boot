package com.ticho.boot.web.config;

import com.ticho.boot.web.handle.TichoTaskDecortor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-01-05 13:08
 */
@Configuration
public class TaskDecoratorConfig {

    @Bean
    public TichoTaskDecortor<RequestAttributes> requestAttributes() {
        TichoTaskDecortor<RequestAttributes> decortor = new TichoTaskDecortor<>();
        decortor.setSupplier(RequestContextHolder::currentRequestAttributes);
        decortor.setExecute(RequestContextHolder::setRequestAttributes);
        decortor.setComplete(x -> RequestContextHolder.resetRequestAttributes());
        return decortor;
    }

    @Bean
    public TichoTaskDecortor<Map<String, String>> mdc() {
        TichoTaskDecortor<Map<String, String>> decortor = new TichoTaskDecortor<>();
        decortor.setSupplier(MDC::getCopyOfContextMap);
        decortor.setExecute(MDC::setContextMap);
        decortor.setComplete(x -> MDC.clear());
        return decortor;
    }

    @Bean
    public TaskDecorator taskDecorator(List<TichoTaskDecortor<?>> taskDecorators) {
        return runnable -> {
            try {
                taskDecorators.forEach(TichoTaskDecortor::setData);
                return () -> {
                    try {
                        taskDecorators.forEach(TichoTaskDecortor::execute);
                        runnable.run();
                    } finally {
                        taskDecorators.forEach(TichoTaskDecortor::complete);
                    }
                };
            } catch (IllegalStateException e) {
                return runnable;
            }
        };
    }

}
