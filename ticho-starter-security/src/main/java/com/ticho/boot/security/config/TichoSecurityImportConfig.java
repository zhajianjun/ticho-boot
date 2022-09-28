package com.ticho.boot.security.config;

import com.ticho.boot.security.auth.AntPatternsAuthHandle;
import com.ticho.boot.security.auth.PermissionService;
import com.ticho.boot.security.auth.PermissionServiceImpl;
import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.filter.TichoAccessDecisionManager;
import com.ticho.boot.security.filter.TichoTokenAuthenticationTokenFilter;
import com.ticho.boot.security.handle.jwt.JwtSigner;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.security.handle.jwt.JwtExtInfo;
import com.ticho.boot.security.handle.jwt.TichoJwtExtInfo;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import com.ticho.boot.security.view.TichoAccessDeniedHandler;
import com.ticho.boot.security.view.TichoAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * springsecurity 相关bean配置
 *
 * @author zhajianjun
 * @date 2022-09-22 10:32
 */
@Configuration
public class TichoSecurityImportConfig {
    // @formatter:off

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(JwtSigner.class)
    public JwtSigner jwtSinger() {
        return new JwtSigner("ticho");
    }

    @Bean(OAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    @ConditionalOnMissingBean(name = OAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    public OncePerRequestFilter tichoToenAuthenticationTokenFilter(){
        return new TichoTokenAuthenticationTokenFilter();
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler(){
        return new TichoAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new TichoAuthenticationEntryPoint();
    }

    @Bean
    public JwtExtInfo jwtExtInfo() {
        return new TichoJwtExtInfo();
    }

    @Bean
    public JwtDecode jwtDecode(JwtSigner jwtSigner) {
        return new JwtDecode(jwtSigner);
    }

    @Bean
    @ConfigurationProperties(prefix = "ticho.security")
    public TichoSecurityProperty tichoSecurityProperty(){
        return new TichoSecurityProperty();
    }

    @Bean
    public TichoAccessDecisionManager tichoAccessDecisionManager(){
        return new TichoAccessDecisionManager();
    }

    @Bean
    public AntPatternsAuthHandle antPatternsAuthHandle(){
        return new AntPatternsAuthHandle();
    }

    @Bean(SecurityConst.PM)
    @ConditionalOnMissingBean(name = SecurityConst.PM)
    public PermissionService permissionService(){
        return new PermissionServiceImpl();
    }

}
