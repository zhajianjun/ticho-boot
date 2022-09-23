package com.ticho.boot.security.handle.load;

import com.ticho.boot.security.dto.SecurityUser;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户查询抽象
 *
 * @author zhajianjun
 * @date 2022-09-22 10:07
 */
public abstract class AbstractUserDetailsStrategy implements UserDetailsService, LoadUserStrategy {

    public SecurityUser loadUserByUsername(String username) {
        return loadUser(username);
    }
}
