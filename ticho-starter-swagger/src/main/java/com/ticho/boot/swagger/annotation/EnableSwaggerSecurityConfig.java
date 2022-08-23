package com.ticho.boot.swagger.annotation;

import com.ticho.boot.swagger.config.SwaggerSecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
// @formatter:off
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
    SwaggerSecurityConfig.class
})
// @formatter:on
public @interface EnableSwaggerSecurityConfig {
}
