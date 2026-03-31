package top.ticho.starter.security.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import top.ticho.tool.core.TiObjUtil;
import top.ticho.tool.json.util.TiJsonUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 接口权限实现
 *
 * @author zhajianjun
 * @date 2022-09-26 17:31
 */
@Slf4j
public class TiPermissionServiceImpl implements TiPermissionService {

    /**
     * 管理员角色标识，拥有管理员角色的用户自动拥有所有权限
     */
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    /**
     * 超级管理员角色标识
     */
    private static final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";

    @Override
    public boolean hasPerms(String... permissions) {
        // 参数校验
        if (TiObjUtil.isEmpty(permissions)) {
            log.warn("权限校验失败：permissions 为空");
            return false;
        }

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            log.warn("权限校验失败：用户未认证");
            return false;
        }

        // 检查是否是管理员角色
        if (hasAdminRole(authentication)) {
            log.debug("权限校验通过：用户拥有管理员角色");
            return true;
        }

        // 获取用户权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            log.warn("权限校验失败：用户没有任何权限");
            return false;
        }

        // 提取用户的权限标识
        Set<String> userPermissions = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            userPermissions.add(authority.getAuthority());
        }

        // 检查用户是否拥有所需权限中的任意一个
        for (String requiredPermission : permissions) {
            if (userPermissions.contains(requiredPermission)) {
                log.debug("权限校验通过：用户拥有权限 {}", requiredPermission);
                return true;
            }
        }

        log.warn("权限校验失败：用户缺少所需权限。需要权限: {}, 用户权限: {}",
            String.join(",", permissions), String.join(",", userPermissions));
        return false;
    }

    /**
     * 检查用户是否拥有管理员角色
     *
     * @param authentication 认证信息
     * @return 是否是管理员
     */
    private boolean hasAdminRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return false;
        }
        for (GrantedAuthority authority : authorities) {
            String authorityStr = authority.getAuthority();
            if (ADMIN_ROLE.equals(authorityStr) || SUPER_ADMIN_ROLE.equals(authorityStr)) {
                return true;
            }
        }
        return false;
    }

}
