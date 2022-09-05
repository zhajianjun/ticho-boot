package com.ticho.boot.swagger.config;

import com.ticho.boot.swagger.prop.SwaggerSecurityProperty;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * swagger2配置
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Configuration
public class SwaggerSecurityConfig {

    private static final String OAUTH_2 = "oauth2";
    private static final String OAUTH_TOKEN = "/oauth/token";
    private static final String OAUTH_AUTHORIZE = "/oauth/authorize";

    @Autowired
    private SwaggerSecurityProperty securityProperty;

    @Autowired
    private ApiInfo apiInfo;

    // @formatter:off

    @Bean
    @ConditionalOnProperty(prefix = "ticho.security", name = "type", havingValue = "password")
    @ConditionalOnMissingBean(Docket.class)
    public Docket passwordDocket() {
        return creteDocket(securityContexts(), passwordSecuritySchemes());
    }

    @Bean
    @ConditionalOnProperty(prefix = "ticho.security", name = "type", havingValue = "authorization")
    @ConditionalOnMissingBean(Docket.class)
    public Docket authorizationCodeDocket() {
        return creteDocket(securityContexts(), authorizationCodeSecuritySchemes());
    }

    //@Bean("implicitSecurityDocket")
    @Bean
    @ConditionalOnProperty(prefix = "ticho.security", name = "type", havingValue = "implicit")
    @ConditionalOnMissingBean(Docket.class)
    public Docket implicitSecurityDocket() {
        return creteDocket(securityContexts(), implicitSecuritySchemes());
    }

    //@Bean("clientCredentialsDocket")
    @Bean
    @ConditionalOnProperty(prefix = "ticho.security", name = "type", havingValue = "clientCredentials")
    @ConditionalOnMissingBean(Docket.class)
    public Docket clientCredentialsDocket() {
        return creteDocket(securityContexts(), clientCredentialsSchemes());
    }

    private Docket creteDocket(List<SecurityContext> securityContexts, List<? extends SecurityScheme> securitySchemes) {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathMapping("/")
            .select()
            //.apis(RequestHandlerSelectors.basePackage("com.ticho"))
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            // 路径使用any风格
            .paths(PathSelectors.any())
            //过滤规则,哪些可以通过
            //.paths(doFilteringRules())
            .build()
            //token验证信息
            .securitySchemes(securitySchemes)
            .securityContexts(securityContexts)
            //文档描叙
            .apiInfo(apiInfo);
    }

    // @formatter:on

    private List<SecurityContext> securityContexts() {
        //context
        List<AuthorizationScope> scopes = new ArrayList<>();
        scopes.add(new AuthorizationScope("read", "read  resources"));
        scopes.add(new AuthorizationScope("write", "write resources"));
        scopes.add(new AuthorizationScope("reads", "read all resources"));
        scopes.add(new AuthorizationScope("writes", "write all resources"));
        scopes.add(new AuthorizationScope("all", "all resources"));
        SecurityReference securityReference = new SecurityReference(OAUTH_2, scopes.toArray(new AuthorizationScope[]{}));
        SecurityContext securityContext = new SecurityContext(Collections.singletonList(securityReference), PathSelectors.ant("/api/**"));
        return Collections.singletonList(securityContext);
    }

    /**
     * 密码模式
     */
    private List<SecurityScheme> passwordSecuritySchemes() {
        String passwordTokenUrl = securityProperty.getUrl() + OAUTH_TOKEN;
        GrantType passwordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(passwordTokenUrl);
        List<GrantType> grantTypes = Stream.of(passwordCredentialsGrant).collect(Collectors.toList());
        OAuth oAuth = new OAuthBuilder().name(OAUTH_2).grantTypes(grantTypes).build();
        return Collections.singletonList(oAuth);
    }

    /**
     * 授权码模式
     */
    private List<SecurityScheme> authorizationCodeSecuritySchemes() {
        String authorizationUrl = securityProperty.getUrl() + OAUTH_AUTHORIZE;
        String tokenEndpointUrl = securityProperty.getUrl() + OAUTH_TOKEN;
        TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint(authorizationUrl, "web", "web");
        TokenEndpoint tokenEndpoint = new TokenEndpoint(tokenEndpointUrl, "");
        GrantType authorizationCodeGrant = new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
        List<GrantType> grantTypes = Stream.of(authorizationCodeGrant).collect(Collectors.toList());
        OAuth oAuth = new OAuthBuilder().name(OAUTH_2).grantTypes(grantTypes).build();
        return Collections.singletonList(oAuth);
    }

    /**
     * 简化模式
     */
    private List<SecurityScheme> implicitSecuritySchemes() {
        String loginEndpointUrl = securityProperty.getUrl() + OAUTH_AUTHORIZE;
        LoginEndpoint tokenEndpoint = new LoginEndpoint(loginEndpointUrl);
        GrantType implicitGrant = new ImplicitGrant(tokenEndpoint, "");
        List<GrantType> grantTypes = Stream.of(implicitGrant).collect(Collectors.toList());
        OAuth oAuth = new OAuthBuilder().name(OAUTH_2).grantTypes(grantTypes).build();
        return Collections.singletonList(oAuth);
    }

    /**
     * 客户端模式
     */
    private List<SecurityScheme> clientCredentialsSchemes() {
        String clientCredentialsUrl = securityProperty.getUrl() + OAUTH_TOKEN;
        GrantType clientCredentialsGrant = new ClientCredentialsGrant(clientCredentialsUrl);
        List<GrantType> grantTypes = Stream.of(clientCredentialsGrant).collect(Collectors.toList());
        OAuth oAuth = new OAuthBuilder().name(OAUTH_2).grantTypes(grantTypes).build();
        return Collections.singletonList(oAuth);
    }

}
