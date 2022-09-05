package com.ticho.boot.swagger.annotation;

import com.ticho.boot.swagger.config.DefaultModelPropertySortPlugin;
import com.ticho.boot.swagger.config.DefaultModelToSwaggerMapper;
import com.ticho.boot.swagger.config.DefaultSwaggerParameterBuilder;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;

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
    DefaultModelToSwaggerMapper.class,
    DefaultSwaggerParameterBuilder.class,
    DefaultModelPropertySortPlugin.class,
    BeanValidatorPluginsConfiguration.class
})
// @formatter:on
public @interface EnableSwaggerSort {
}
