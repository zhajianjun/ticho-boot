package top.ticho.boot.security.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import top.ticho.boot.view.core.BaseSecurityUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhajianjun
 * @date 2022-09-30 10:45
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseUserUtil {


    public static <T extends BaseSecurityUser> T getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principalObj = authentication.getPrincipal();
         // 直接检查是否为匿名用户或认证对象为空
        if (principalObj == null || principalObj instanceof String) {
            return null;
        }
        // 安全地进行类型转换
        @SuppressWarnings("unchecked")
        T principal = (T) principalObj;
        return principal;
    }

    public static String getCurrentUsername() {
        BaseSecurityUser user = getCurrentUser();
        return Optional.ofNullable(user).map(BaseSecurityUser::getUsername).orElse(null);
    }

    public static List<String> getRoleIds() {
        BaseSecurityUser user = getCurrentUser();
        return Optional.ofNullable(user).map(BaseSecurityUser::getRoles).orElseGet(ArrayList::new);
    }

}
