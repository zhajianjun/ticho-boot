package com.ticho.boot.swagger.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.List;

/**
 * swagger2配置
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Value("${spring.application.name:ticho-boot-demo}")
    private String applicationName;

    // @formatter:off

    @Bean
    @ConditionalOnProperty(value = "ticho.security.type", havingValue = "default", matchIfMissing = true)
    @ConditionalOnMissingBean(Docket.class)
    public Docket docket(ApiInfo apiInfo) {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathMapping("/")
            .select()
            //.apis(RequestHandlerSelectors.basePackage("com.ticho.modules"))
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            // 路径使用any风格
            .paths(PathSelectors.any())
            //过滤规则,哪些可以通过
            //.paths(doFilteringRules())
            .build()
            //token验证信息
            .securitySchemes(securitySchemes())
            .securityContexts(securityContexts())
            //文档描叙
            .apiInfo(apiInfo);
    }

    /**
     * 接口文档简介
     */
    @Bean
    @ConditionalOnMissingBean(ApiInfo.class)
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(applicationName)
            .version("1.0.0")
            .description("")
            .license("The Apache License")
            .licenseUrl("http://www.ticho.top")
            .termsOfServiceUrl("http://www.ticho.top")
            .contact(new Contact("zhajianjun", "http://www.ticho.top", "1019319474@qq.com"))
            .build();
    }

    public List<SecurityContext> securityContexts() {
        AuthorizationScope all = new AuthorizationScope("global", "accessEverything");
        SecurityReference authorization = new SecurityReference("BearerToken", new AuthorizationScope[]{all});
        SecurityContext securityContext = SecurityContext
            .builder()
            .securityReferences(Collections.singletonList(authorization))
            .forPaths(PathSelectors.regex("^(?!auth).*$"))
            .build();
        return Collections.singletonList(securityContext);
    }

    public List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("BearerToken", "Authorization", "header");
        return Collections.singletonList(apiKey);
    }
}
