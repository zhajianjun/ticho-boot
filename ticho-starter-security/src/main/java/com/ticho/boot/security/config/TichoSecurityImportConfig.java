package com.ticho.boot.security.config;

import com.ticho.boot.security.auth.AntPatternsAuthHandle;
import com.ticho.boot.security.auth.PermissionService;
import com.ticho.boot.security.auth.TichoPermissionServiceImpl;
import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.filter.TichoAccessDecisionManager;
import com.ticho.boot.security.filter.TichoTokenAuthenticationTokenFilter;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.security.handle.jwt.JwtExtra;
import com.ticho.boot.security.handle.jwt.JwtSigner;
import com.ticho.boot.security.handle.jwt.TichoJwtExtra;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import com.ticho.boot.security.view.TichoAccessDeniedHandler;
import com.ticho.boot.security.view.TichoAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
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

    /**
     * 密码编码器
     *
     * @return {@link PasswordEncoder}
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * jwt签名
     *
     * @return {@link JwtSigner}
     */
    @Bean
    @ConditionalOnMissingBean(JwtSigner.class)
    public JwtSigner jwtSinger() {
        return new JwtSigner("ticho");
    }

    @Bean(OAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    @ConditionalOnMissingBean(name = OAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    public OncePerRequestFilter tichoToenAuthenticationTokenFilter() {
        return new TichoTokenAuthenticationTokenFilter();
    }

    /**
     * 拒绝访问处理程序
     *
     * @return {@link AccessDeniedHandler}
     */
    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler() {
        return new TichoAccessDeniedHandler();
    }

    /**
     * 认证入口点
     *
     * @return {@link AuthenticationEntryPoint}
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new TichoAuthenticationEntryPoint();
    }

    /**
     * jwt 扩展信息
     *
     * @return {@link JwtExtra}
     */
    @Bean
    public JwtExtra jwtExtra() {
        return new TichoJwtExtra();
    }

    /**
     * jwt解码
     *
     * @param jwtSigner jwt签名
     * @return {@link JwtDecode}
     */
    @Bean
    public JwtDecode jwtDecode(JwtSigner jwtSigner) {
        return new JwtDecode(jwtSigner);
    }

    /**
     * security参数配置对象
     *
     * @return {@link TichoSecurityProperty}
     */
    @Bean
    @ConfigurationProperties(prefix = "ticho.security")
    public TichoSecurityProperty tichoSecurityProperty() {
        return new TichoSecurityProperty();
    }

    /**
     * ticho访问决策管理
     *
     * @return {@link TichoAccessDecisionManager}
     */
    @Bean
    public TichoAccessDecisionManager tichoAccessDecisionManager() {
        return new TichoAccessDecisionManager();
    }

    @Bean
    public AntPatternsAuthHandle antPatternsAuthHandle() {
        return new AntPatternsAuthHandle();
    }

    /**
     * 权限许可服务
     *
     * @return {@link PermissionService}
     */
    @Bean(SecurityConst.PM)
    @ConditionalOnMissingBean(name = SecurityConst.PM)
    public PermissionService permissionService() {
        return new TichoPermissionServiceImpl();
    }

    /**
     * 处理安全控制台打印
     *
     * @see UserDetailsServiceAutoConfiguration 87行 会打印存储在内存的权限用户密码
     */
    @Autowired
    public void handleConsoleSecurityPrint(SecurityProperties properties) {
        SecurityProperties.User user = properties.getUser();
        user.setPassword("user");
    }

}
