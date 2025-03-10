package top.ticho.starter.swagger.config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationCodeGrant;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ClientCredentialsGrant;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import top.ticho.starter.swagger.prop.BaseSwaggerSecurityProperty;

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
@ConditionalOnBean(BaseSwaggerConfig.class)
public class BaseSwaggerSecurityConfig {

    private static final String OAUTH_2 = "oauth2";
    private static final String OAUTH_TOKEN = "/oauth/token";
    private static final String OAUTH_AUTHORIZE = "/oauth/authorize";

    @Autowired
    private BaseSwaggerSecurityProperty baseSwaggerSecurityProperty;

    @Autowired(required = false)
    private ApiInfo apiInfo;


    @Bean
    @ConditionalOnProperty(value = BaseSwaggerConfig.TYPE, havingValue = "password")
    @ConditionalOnMissingBean(Docket.class)
    public Docket passwordDocket() {
        return creteDocket(securityContexts(), passwordSecuritySchemes());
    }

    @Bean
    @ConditionalOnProperty(value = BaseSwaggerConfig.TYPE, havingValue = "authorization")
    @ConditionalOnMissingBean(Docket.class)
    public Docket authorizationCodeDocket() {
        return creteDocket(securityContexts(), authorizationCodeSecuritySchemes());
    }

    //@Bean("implicitSecurityDocket")
    @Bean
    @ConditionalOnProperty(value = BaseSwaggerConfig.TYPE, havingValue = "implicit")
    @ConditionalOnMissingBean(Docket.class)
    public Docket implicitSecurityDocket() {
        return creteDocket(securityContexts(), implicitSecuritySchemes());
    }

    //@Bean("clientCredentialsDocket")
    @Bean
    @ConditionalOnProperty(value = BaseSwaggerConfig.TYPE, havingValue = "clientCredentials")
    @ConditionalOnMissingBean(Docket.class)
    public Docket clientCredentialsDocket() {
        return creteDocket(securityContexts(), clientCredentialsSchemes());
    }

    private Docket creteDocket(List<SecurityContext> securityContexts, List<SecurityScheme> securitySchemes) {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathMapping("/")
            .select()
            //.apis(RequestHandlerSelectors.basePackage("top.ticho"))
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            // 路径使用any风格
            .paths(PathSelectors.any())
            // 过滤规则,哪些可以通过
            //.paths(doFilteringRules())
            .build()
            // token验证信息
            .securitySchemes(securitySchemes)
            .securityContexts(securityContexts)
            // 文档描叙
            .apiInfo(apiInfo);
    }


    private List<SecurityContext> securityContexts() {
        // context
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
        String passwordTokenUrl = baseSwaggerSecurityProperty.getSecurityUrl() + OAUTH_TOKEN;
        GrantType passwordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(passwordTokenUrl);
        List<GrantType> grantTypes = Stream.of(passwordCredentialsGrant).collect(Collectors.toList());
        OAuth oAuth = new OAuthBuilder().name(OAUTH_2).grantTypes(grantTypes).build();
        return Collections.singletonList(oAuth);
    }

    /**
     * 授权码模式
     */
    private List<SecurityScheme> authorizationCodeSecuritySchemes() {
        String authorizationUrl = baseSwaggerSecurityProperty.getSecurityUrl() + OAUTH_AUTHORIZE;
        String tokenEndpointUrl = baseSwaggerSecurityProperty.getSecurityUrl() + OAUTH_TOKEN;
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
        String loginEndpointUrl = baseSwaggerSecurityProperty.getSecurityUrl() + OAUTH_AUTHORIZE;
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
        String clientCredentialsUrl = baseSwaggerSecurityProperty.getSecurityUrl() + OAUTH_TOKEN;
        GrantType clientCredentialsGrant = new ClientCredentialsGrant(clientCredentialsUrl);
        List<GrantType> grantTypes = Stream.of(clientCredentialsGrant).collect(Collectors.toList());
        OAuth oAuth = new OAuthBuilder().name(OAUTH_2).grantTypes(grantTypes).build();
        return Collections.singletonList(oAuth);
    }

}
