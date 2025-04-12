package top.ticho.starter.security.config;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@AutoConfigureBefore(ManagementWebSecurityAutoConfiguration.class)
public class TiWebSecurityConfig {

    @Resource
    @Qualifier(TiSecurityConst.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    private OncePerRequestFilter oncePerRequestFilter;
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(item -> item.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(item -> {
                item.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // 动态给特定资源接口放行
                    .anyRequest().access(new TiAuthorizationManager())
                    .anyRequest().authenticated();
            })
            .headers(item -> item.cacheControl(cacheControlConfig -> {
            }))
            .addFilterBefore(oncePerRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(item -> {
                item            // 无Authorization相关header参数
                    .accessDeniedHandler(accessDeniedHandler)
                    // 认证成功,权限不足返回视图
                    .authenticationEntryPoint(authenticationEntryPoint);
            }).build();

    }

}