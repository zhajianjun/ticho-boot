package top.ticho.starter.security.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ticho.starter.security.constant.TiSecurityConst;

import javax.annotation.Resource;


/**
 * springsecurity 配置
 *
 * @author zhajianjun
 * @date 2022-09-23 15:35:33
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AutoConfigureBefore(ManagementWebSecurityAutoConfiguration.class)
public class TiWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    @Qualifier(TiSecurityConst.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    private OncePerRequestFilter oncePerRequestFilter;

    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    @Resource
    private AccessDecisionManager accessDecisionManager;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 动态给特定资源接口放行
            .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                    // 权限判断
                    o.setAccessDecisionManager(accessDecisionManager);
                    return o;
                }
            })
            .anyRequest().authenticated()
            .and()
            .headers()
            .cacheControl();
        http.addFilterBefore(oncePerRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling()
            // 无Authorization相关header参数
            .accessDeniedHandler(accessDeniedHandler)
            // 认证成功,权限不足返回视图
            .authenticationEntryPoint(authenticationEntryPoint);
    }

}