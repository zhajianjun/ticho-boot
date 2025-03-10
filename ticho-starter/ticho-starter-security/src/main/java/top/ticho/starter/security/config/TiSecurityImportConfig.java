package top.ticho.starter.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ticho.starter.security.auth.AntPatternsAuthHandle;
import top.ticho.starter.security.auth.PermissionService;
import top.ticho.starter.security.auth.TiPermissionServiceImpl;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.filter.TiAccessDecisionManager;
import top.ticho.starter.security.filter.TiTokenAuthenticationTokenFilter;
import top.ticho.starter.security.handle.jwt.JwtDecode;
import top.ticho.starter.security.handle.jwt.JwtExtra;
import top.ticho.starter.security.handle.jwt.JwtSigner;
import top.ticho.starter.security.handle.jwt.TiJwtExtra;
import top.ticho.starter.security.prop.TiSecurityProperty;
import top.ticho.starter.security.view.TiAccessDeniedHandler;
import top.ticho.starter.security.view.TiAuthenticationEntryPoint;
import top.ticho.starter.view.task.TiTaskDecortor;

/**
 * springsecurity 相关bean配置
 *
 * @author zhajianjun
 * @date 2022-09-22 10:32
 */
@Configuration
public class TiSecurityImportConfig {

    /**
     * 密码编码器
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * jwt签名
     */
    @Bean
    @ConditionalOnMissingBean(JwtSigner.class)
    public JwtSigner jwtSigner() {
        return new JwtSigner("ticho");
    }

    @Bean(TiSecurityConst.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    @ConditionalOnMissingBean(name = TiSecurityConst.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    public OncePerRequestFilter baseTokenAuthenticationTokenFilter() {
        return new TiTokenAuthenticationTokenFilter();
    }

    /**
     * 拒绝访问处理程序
     */
    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler() {
        return new TiAccessDeniedHandler();
    }

    /**
     * 认证入口点
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new TiAuthenticationEntryPoint();
    }

    /**
     * jwt 扩展信息
     */
    @Bean
    public JwtExtra jwtExtra() {
        return new TiJwtExtra();
    }

    /**
     * jwt解码
     */
    @Bean
    public JwtDecode jwtDecode(JwtSigner jwtSigner) {
        return new JwtDecode(jwtSigner);
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
     * ticho访问决策管理
     */
    @Bean
    public TiAccessDecisionManager tichoAccessDecisionManager() {
        return new TiAccessDecisionManager();
    }

    @Bean
    public AntPatternsAuthHandle antPatternsAuthHandle(TiSecurityProperty tiSecurityProperty) {
        return new AntPatternsAuthHandle(tiSecurityProperty);
    }

    /**
     * 权限许可服务
     */
    @Bean(TiSecurityConst.PM)
    @ConditionalOnMissingBean(name = TiSecurityConst.PM)
    public PermissionService permissionService() {
        return new TiPermissionServiceImpl();
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

}
