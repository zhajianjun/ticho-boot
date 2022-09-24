package com.ticho.boot.security.handle;

import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 根据用户名查询
 *
 * @author zhajianjun
 * @date 2022-09-22 11:17
 */
@Component(SecurityConst.LOAD_USER_TYPE_USERNAME)
@ConditionalOnMissingBean(name = SecurityConst.LOAD_USER_TYPE_USERNAME)
@Primary
@Slf4j
public class TichoUsernameUserDetailsService implements UserDetailsService {

    @Resource
    private TichoSecurityProperty tichoSecurityProperty;

    @Override
    public UserDetails loadUserByUsername(String account) {
        // @formatter:off
        List<TichoSecurityProperty.User> users = tichoSecurityProperty.getUser();
        return users
            .stream()
            .filter(x-> Objects.equals(x.getUsername(), account))
            .findFirst()
            .orElse(null);
        // @formatter:on
    }
}
