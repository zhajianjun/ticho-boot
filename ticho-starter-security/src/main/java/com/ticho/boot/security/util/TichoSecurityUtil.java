package com.ticho.boot.security.util;

import com.ticho.boot.view.core.TichoSecurityUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-30 10:45
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TichoSecurityUtil {


    public static <T extends TichoSecurityUser> T getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }
        // 匿名用户返回null
        if (principal instanceof String) {
            return null;
        }
        return (T) principal;
    }

    public static String getCurrentUsername() {
        TichoSecurityUser user = getCurrentUser();
        return Optional.ofNullable(user).map(TichoSecurityUser::getUsername).orElse(null);
    }

    public static List<String> getRoleIds() {
        TichoSecurityUser user = getCurrentUser();
        return Optional.ofNullable(user).map(TichoSecurityUser::getRoleIds).orElseGet(ArrayList::new);
    }

}
