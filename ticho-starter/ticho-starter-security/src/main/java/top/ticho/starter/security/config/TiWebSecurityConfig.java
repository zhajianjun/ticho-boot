package top.ticho.starter.security.config;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import top.ticho.starter.security.auth.TiAntPatternsAuthHandle;
import top.ticho.starter.security.auth.TiPermissionService;
import top.ticho.starter.security.auth.TiPermissionServiceImpl;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.controller.TiLoginServiceImpl;
import top.ticho.starter.security.controller.TiOauthController;
import top.ticho.starter.security.core.TiAccessDeniedHandler;
import top.ticho.starter.security.core.TiAuthenticationEntryPoint;
import top.ticho.starter.security.core.jwt.TiTokenExtra;
import top.ticho.starter.security.core.jwt.TiDefaultTiTokenExtra;
import top.ticho.starter.security.filter.AbstractAuthTokenFilter;
import top.ticho.starter.security.filter.TiAuthorizationManager;
import top.ticho.starter.security.filter.TiTokenAuthenticationTokenFilter;
import top.ticho.starter.security.prop.TiSecurityProperty;
import top.ticho.starter.security.service.TiLoginService;
import top.ticho.starter.view.task.TiTaskDecortor;


/**
 * springsecurity 配置
 *
 * @author zhajianjun
 * @date 2022-09-23 15:35
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class TiWebSecurityConfig {

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

    @Bean
    @Order(100)
    public SecurityFilterChain tiSecurityFilterChain(HttpSecurity http, TiAntPatternsAuthHandle tiAntPatternsAuthHandle, ObjectProvider<AbstractAuthTokenFilter<?>> abstractAuthTokenFilterProvider) throws Exception {
        AbstractAuthTokenFilter<?> abstractAuthTokenFilter = abstractAuthTokenFilterProvider.getIfAvailable(TiTokenAuthenticationTokenFilter::new);
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
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(registry -> registry.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest().access(new TiAuthorizationManager(tiAntPatternsAuthHandle))).securityMatcher(request -> true)
            // 显式声明禁用缓存控制
            .headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable)).addFilterBefore(abstractAuthTokenFilter, LogoutFilter.class).exceptionHandling(handling -> handling
                // 先处理未认证
                .authenticationEntryPoint(new TiAuthenticationEntryPoint())
                // 后处理权限不足
                .accessDeniedHandler(new TiAccessDeniedHandler()));
        return http.build();
    }

    /**
     * security参数配置对象
     */
    @Bean
    @ConfigurationProperties(prefix = "ticho.security")
    public TiSecurityProperty tichoSecurityProperty() {
        return new TiSecurityProperty();
    }

    /**
     * jwt 扩展信息
     */
    @Bean
    public TiTokenExtra jwtExtra() {
        return new TiDefaultTiTokenExtra();
    }

    /**
     * 密码编码器
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 权限许可服务
     */
    @Bean(TiSecurityConst.PM)
    public TiPermissionService tiPermissionService() {
        return new TiPermissionServiceImpl();
    }

    @Bean
    public TiAntPatternsAuthHandle antPatternsAuthHandle(TiSecurityProperty tiSecurityProperty) {
        return new TiAntPatternsAuthHandle(tiSecurityProperty);
    }

    /**
     * 权限上下文传递
     */
    @Bean
    public TiTaskDecortor<Authentication> authenticationTaskDecortor() {
        TiTaskDecortor<Authentication> decortor = new TiTaskDecortor<>();
        decortor.setSupplier(() -> SecurityContextHolder.getContext().getAuthentication());
        decortor.setExecute((x) -> SecurityContextHolder.getContext().setAuthentication(x));
        decortor.setComplete(x -> SecurityContextHolder.clearContext());
        return decortor;
    }

    @Bean
    @ConditionalOnMissingBean(TiLoginService.class)
    public TiLoginService tiLoginService(TiSecurityProperty tiSecurityProperty, PasswordEncoder passwordEncoder) {
        return new TiLoginServiceImpl(tiSecurityProperty, passwordEncoder);
    }

    @Bean
    @ConditionalOnProperty(value = "ticho.security.web.enable", havingValue = "true")
    public TiOauthController tiOauthController(TiLoginService tiLoginService) {
        return new TiOauthController(tiLoginService);
    }

}