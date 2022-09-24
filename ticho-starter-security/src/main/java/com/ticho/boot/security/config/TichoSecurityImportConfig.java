package com.ticho.boot.security.config;

import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.filter.TichoToenAuthenticationTokenFilter;
import com.ticho.boot.security.handle.jwt.JwtConverter;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.security.handle.jwt.JwtExtInfo;
import com.ticho.boot.security.handle.jwt.TichoJwtExtInfo;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import com.ticho.boot.security.view.TichoAccessDenyFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

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
    @ConditionalOnMissingBean(JwtConverter.class)
    public JwtConverter jwtConverter() {
        return new JwtConverter("ticho");
    }

    @Bean
    public UserDetailsChecker userDetailsChecker() {
        return new AccountStatusUserDetailsChecker();
    }

    @Bean(OAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    @ConditionalOnMissingBean(name = OAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    public TichoToenAuthenticationTokenFilter tichoToenAuthenticationTokenFilter(){
        return new TichoToenAuthenticationTokenFilter();
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler(){
        return new TichoAccessDenyFilter();
    }

    @Bean
    public JwtExtInfo jwtExtInfo() {
        return new TichoJwtExtInfo();
    }

    @Bean
    public JwtDecode jwtDecode(JwtConverter jwtConverter) {
        return new JwtDecode(jwtConverter);
    }

    @Bean
    @ConfigurationProperties(prefix = "ticho.security")
    public TichoSecurityProperty tichoSecurityProperty(){
        return new TichoSecurityProperty();
    }

}
