package com.ticho.boot.swagger.config;


import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Optional;

import static springfox.documentation.schema.Annotations.findPropertyAnnotation;
import static springfox.documentation.swagger.schema.ApiModelProperties.findApiModePropertyAnnotation;

/**
 * swagger2 实体类成员变量顺序按照代码顺序而不是字母顺序
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Component
@ConditionalOnBean(BaseSwaggerConfig.class)
public class BaseModelPropertySortPlugin implements ModelPropertyBuilderPlugin {
    private static final Logger log = LoggerFactory.getLogger(BaseModelPropertySortPlugin.class);

    public static int indexOf(Object[] array, Object objectToFind) {
        if (array != null) {
            int i;
            if (objectToFind == null) {
                for (i = 0; i < array.length; ++i) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            } else {
                for (i = 0; i < array.length; ++i) {
                    if (objectToFind.equals(array[i])) {
                        return i;
                    }
                }
            }

        }
        return -1;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<BeanPropertyDefinition> beanPropertyDefinitionOpt = context.getBeanPropertyDefinition();
        Optional<ApiModelProperty> apiModelProperty = Optional.empty();
        Optional<AnnotatedElement> annotatedElement = context.getAnnotatedElement();
        if (annotatedElement.isPresent()) {
            apiModelProperty = findApiModePropertyAnnotation(annotatedElement.get());
        }
        if (beanPropertyDefinitionOpt.isPresent()) {
            apiModelProperty = findPropertyAnnotation(beanPropertyDefinitionOpt.get(), ApiModelProperty.class);
        }
        if (beanPropertyDefinitionOpt.isPresent()) {
            BeanPropertyDefinition beanPropertyDefinition = beanPropertyDefinitionOpt.get();
            if (!apiModelProperty.isPresent() || apiModelProperty.get().position() != 0) {
                return;
            }
            AnnotatedField field = beanPropertyDefinition.getField();
            Class<?> clazz = field.getDeclaringClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            Field declaredField;
            try {
                declaredField = clazz.getDeclaredField(field.getName());
            } catch (NoSuchFieldException | SecurityException e) {
                log.error("", e);
                return;
            }
            int indexOf = indexOf(declaredFields, declaredField);
            if (indexOf != -1) {
                context.getBuilder().position(indexOf);
            }
        }
    }
}
