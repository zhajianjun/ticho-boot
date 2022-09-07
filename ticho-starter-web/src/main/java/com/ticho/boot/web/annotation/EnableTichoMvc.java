package com.ticho.boot.web.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @formatter:off

/**
 * 加载一些bean
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
    com.ticho.boot.web.config.TichoAsyncConfig.class,
    com.ticho.boot.web.config.TichoMvcConfig.class,
    com.ticho.boot.web.config.TichoCorsConfig.class,
    com.ticho.boot.web.config.TichoJacksonCustomizerConfig.class,
    com.ticho.boot.web.controller.HeathController.class,
    com.ticho.boot.web.handle.TichoBaseResponseHandle.class,
    com.ticho.boot.web.converter.DateConverter.class,
    com.ticho.boot.web.converter.LocalDateConverter.class,
    com.ticho.boot.web.converter.LocalDateTimeConverter.class,
    com.ticho.boot.web.event.TichoApplicationReadyEvent.class,
    com.ticho.boot.web.util.CloudIdUtil.class
})
public @interface EnableTichoMvc {
}
