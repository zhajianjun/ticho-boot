package top.ticho.boot.security.filter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import top.ticho.boot.security.auth.AntPatternsAuthHandle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 自定义权限管理
 *
 * @author zhajianjun
 * @date 2020-07-03 15:39
 * @link <a href="https://blog.csdn.net/u012373815/article/details/54633046">...</a>
 */
@Slf4j
public class BaseAccessDecisionManager implements AccessDecisionManager {

    @Resource
    private AntPatternsAuthHandle antPatternsAuthHandle;


    /**
     * decide 方法是判定是否拥有权限的决策方法，
     * authentication 是释CustomUserService中循环添加到 GrantedAuthority 对象中的权限信息集合.
     * object 包含客户端发起的请求的requset信息，可转换为 HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
     * configAttributes 为MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回的结果，此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
     */
    @SneakyThrows
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        if (antPatternsAuthHandle.ignoreAuth(request)) {
            return;
        }
        // 非放行的url没有权限信息 默认是 AnonymousAuthenticationToken
        if (authentication instanceof AnonymousAuthenticationToken) {
            // 说明没有token传入，所以没有权限信息，即是没有用户信息，即没有登录，抛出异常会
            throw new AccessDeniedException("无访问权限");
        }
    }

    /**
     * 复制默认方法，使得@PreAuthorize("hasRole('ROLE_ADMIN')") 可用
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
