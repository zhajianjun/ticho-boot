package top.ticho.boot.security.config;

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
import top.ticho.boot.security.auth.AntPatternsAuthHandle;
import top.ticho.boot.security.auth.BasePermissionServiceImpl;
import top.ticho.boot.security.auth.PermissionService;
import top.ticho.boot.security.constant.BaseOAuth2Const;
import top.ticho.boot.security.constant.BaseSecurityConst;
import top.ticho.boot.security.filter.BaseAccessDecisionManager;
import top.ticho.boot.security.filter.BaseTokenAuthenticationTokenFilter;
import top.ticho.boot.security.handle.jwt.BaseJwtExtra;
import top.ticho.boot.security.handle.jwt.JwtDecode;
import top.ticho.boot.security.handle.jwt.JwtExtra;
import top.ticho.boot.security.handle.jwt.JwtSigner;
import top.ticho.boot.security.prop.BaseSecurityProperty;
import top.ticho.boot.security.view.BaseAccessDeniedHandler;
import top.ticho.boot.security.view.BaseAuthenticationEntryPoint;
import top.ticho.boot.view.task.TiTaskDecortor;

/**
 * springsecurity 相关bean配置
 *
 * @author zhajianjun
 * @date 2022-09-22 10:32
 */
@Configuration
public class BaseSecurityImportConfig {

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
    public JwtSigner jwtSigner() {
        return new JwtSigner("ticho");
    }

    @Bean(BaseOAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    @ConditionalOnMissingBean(name = BaseOAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
    public OncePerRequestFilter baseTokenAuthenticationTokenFilter() {
        return new BaseTokenAuthenticationTokenFilter();
    }

    /**
     * 拒绝访问处理程序
     *
     * @return {@link AccessDeniedHandler}
     */
    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler accessDeniedHandler() {
        return new BaseAccessDeniedHandler();
    }

    /**
     * 认证入口点
     *
     * @return {@link AuthenticationEntryPoint}
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new BaseAuthenticationEntryPoint();
    }

    /**
     * jwt 扩展信息
     *
     * @return {@link JwtExtra}
     */
    @Bean
    public JwtExtra jwtExtra() {
        return new BaseJwtExtra();
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
     * @return {@link BaseSecurityProperty}
     */
    @Bean
    @ConfigurationProperties(prefix = "ticho.security")
    public BaseSecurityProperty tichoSecurityProperty() {
        return new BaseSecurityProperty();
    }

    /**
     * ticho访问决策管理
     *
     * @return {@link BaseAccessDecisionManager}
     */
    @Bean
    public BaseAccessDecisionManager tichoAccessDecisionManager() {
        return new BaseAccessDecisionManager();
    }

    @Bean
    public AntPatternsAuthHandle antPatternsAuthHandle(BaseSecurityProperty baseSecurityProperty) {
        return new AntPatternsAuthHandle(baseSecurityProperty);
    }

    /**
     * 权限许可服务
     *
     * @return {@link PermissionService}
     */
    @Bean(BaseSecurityConst.PM)
    @ConditionalOnMissingBean(name = BaseSecurityConst.PM)
    public PermissionService permissionService() {
        return new BasePermissionServiceImpl();
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
     *
     * @return {@link TiTaskDecortor}<{@link Authentication}>
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
