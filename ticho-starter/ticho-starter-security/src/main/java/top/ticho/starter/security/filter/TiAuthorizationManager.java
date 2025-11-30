package top.ticho.starter.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import top.ticho.starter.security.auth.TiAntPatternsAuthHandle;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

/**
 * 自定义权限管理
 *
 * @author zhajianjun
 * @date 2020-07-03 15:39
 * @link <a href="https://blog.csdn.net/u012373815/article/details/54633046">...</a>
 */
@Slf4j
@RequiredArgsConstructor
public class TiAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private final TiAntPatternsAuthHandle tiAntPatternsAuthHandle;

    /**
     * decide 方法是判定是否拥有权限的决策方法，
     * authentication 是释CustomUserService中循环添加到 GrantedAuthority 对象中的权限信息集合.
     * object 包含客户端发起的请求的requset信息，可转换为 HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
     * configAttributes 为MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回的结果，此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
     */
    @Override
    public AuthorizationResult authorize(@NonNull Supplier<? extends Authentication> authentication, @NonNull RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();
        if (tiAntPatternsAuthHandle.ignoreAuth(request) || isGranted(authentication.get())) {
            return new AuthorizationDecision(true);
        }
        throw new AccessDeniedException("无访问权限");
    }

    private boolean isGranted(Authentication authentication) {
        return authentication != null && !this.trustResolver.isAnonymous(authentication)
            && authentication.isAuthenticated();
    }

}
