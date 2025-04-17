package top.ticho.starter.security.config;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.filter.TiAuthorizationManager;


/**
 * springsecurity 配置
 *
 * @author zhajianjun
 * @date 2022-09-23 15:35:33
 */
@Configuration
@EnableWebSecurity
public class TiWebSecurityConfig {

    @Resource
    @Qualifier(TiSecurityConst.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    private OncePerRequestFilter oncePerRequestFilter;
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    @Order(100)
    public SecurityFilterChain tiSecurityFilterChain(HttpSecurity http, TiAuthorizationManager authorizationManager) throws Exception {
        http
            // 禁用表单登录
            .formLogin(AbstractHttpConfigurer::disable)
            // 禁用httpBasic登录
            .httpBasic(AbstractHttpConfigurer::disable)
            // 禁用rememberMe
            .rememberMe(AbstractHttpConfigurer::disable)
            // 关闭csrf
            .csrf(AbstractHttpConfigurer::disable)
            // 允许跨域请求
            .cors(Customizer.withDefaults())
            // 关闭csrf
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用session
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(registry -> registry
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().access(authorizationManager)
            )
            .securityMatcher(request -> true)
            // 显式声明禁用缓存控制
            .headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable))
            .addFilterBefore(oncePerRequestFilter, LogoutFilter.class)
            .exceptionHandling(handling -> handling
                // 先处理未认证
                .authenticationEntryPoint(authenticationEntryPoint)
                // 后处理权限不足
                .accessDeniedHandler(accessDeniedHandler)
            );
        return http.build();
    }

}