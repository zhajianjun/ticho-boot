package com.ticho.boot.swagger.config;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.readers.parameter.Examples;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER;

/**
 * 定义ExpandedParameterBuilderPlugin，主要是修正源码query传入请求参数postion无效
 * 这里，将postion赋值给order
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@ConditionalOnBean(TichoSwaggerConfig.class)
@Primary
@Component
public class TichoSwaggerParameterBuilder implements ExpandedParameterBuilderPlugin {

    private final DescriptionResolver descriptions;
    private final EnumTypeDeterminer enumTypeDeterminer;

    @Autowired(required = false)
    public TichoSwaggerParameterBuilder(DescriptionResolver descriptions, EnumTypeDeterminer enumTypeDeterminer) {
        this.descriptions = descriptions;
        this.enumTypeDeterminer = enumTypeDeterminer;
    }

    @Override
    public void apply(ParameterExpansionContext context) {
        Optional<ApiModelProperty> apiModelPropertyOptional = context.findAnnotation(ApiModelProperty.class);
        apiModelPropertyOptional.ifPresent(apiModelProperty -> fromApiModelProperty(context, apiModelProperty));
        Optional<ApiParam> apiParamOptional = context.findAnnotation(ApiParam.class);
        apiParamOptional.ifPresent(apiParam -> fromApiParam(context, apiParam));
    }

    @Override
    public boolean supports(@NonNull DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    private void fromApiParam(ParameterExpansionContext context, ApiParam apiParam) {
        // @formatter:off
        String allowableProperty = apiParam.allowableValues();
        AllowableValues allowable = allowableValues(Optional.of(allowableProperty), context.getFieldType().getErasedType());
        maybeSetParameterName(context, apiParam.name())
            .description(descriptions.resolve(apiParam.value()))
            .defaultValue(apiParam.defaultValue())
            .required(apiParam.required())
            .allowMultiple(apiParam.allowMultiple())
            .allowableValues(allowable)
            .parameterAccess(apiParam.access())
            .hidden(apiParam.hidden())
            .scalarExample(apiParam.example())
            .complexExamples(Examples.examples(apiParam.examples()))
            .order(SWAGGER_PLUGIN_ORDER).build();
        // @formatter:on
    }

    private void fromApiModelProperty(ParameterExpansionContext context, ApiModelProperty apiModelProperty) {
        // @formatter:off
        String allowableProperty = apiModelProperty.allowableValues();
        AllowableValues allowable = allowableValues(Optional.of(allowableProperty), context.getFieldType().getErasedType());
        maybeSetParameterName(context, apiModelProperty.name())
            .description(descriptions.resolve(apiModelProperty.value()))
            .required(apiModelProperty.required())
            .allowableValues(allowable)
            .parameterAccess(apiModelProperty.access())
            .hidden(apiModelProperty.hidden())
            .scalarExample(apiModelProperty.example())
            //源码这里是: SWAGGER_PLUGIN_ORDER，需要修正
            .order(apiModelProperty.position()).build();
        // @formatter:on
    }

    private ParameterBuilder maybeSetParameterName(ParameterExpansionContext context, String parameterName) {
        if (parameterName != null && !parameterName.isEmpty()) {
            context.getParameterBuilder().name(parameterName);
        }
        return context.getParameterBuilder();
    }

    private AllowableValues allowableValues(final Optional<String> optionalAllowable, Class<?> fieldType) {

        AllowableValues allowable = null;
        if (enumTypeDeterminer.isEnum(fieldType)) {
            allowable = new AllowableListValues(getEnumValues(fieldType), "LIST");
        } else if (optionalAllowable.isPresent()) {
            allowable = ApiModelProperties.allowableValueFromString(optionalAllowable.get());
        }
        return allowable;
    }

    private List<String> getEnumValues(final Class<?> subject) {
        return Arrays.<Object>stream(subject.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
    }
}