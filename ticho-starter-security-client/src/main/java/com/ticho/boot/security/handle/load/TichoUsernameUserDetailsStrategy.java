package com.ticho.boot.security.handle.load;

import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.dto.SecurityUser;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
public class TichoUsernameUserDetailsStrategy extends AbstractUserDetailsStrategy {

    @Resource
    private TichoSecurityProperty tichoSecurityProperty;

    @Override
    public SecurityUser loadUser(String account) {
        // @formatter:off
        List<TichoSecurityProperty.User> users = tichoSecurityProperty.getUser();
        return users
            .stream()
            .filter(x-> Objects.equals(x.getUsername(), account))
            .map(this::convert)
            .findFirst()
            .orElse(null);
        // @formatter:on
    }

    private SecurityUser convert(TichoSecurityProperty.User user) {
        // @formatter:off
        SecurityUser securityUser = new SecurityUser();
        securityUser.setUsername(user.getUsername());
        securityUser.setPassword(user.getPassword());
        securityUser.setStatus(1);
        List<String> roleIds = user.getRole();
        List<SimpleGrantedAuthority> authorities = roleIds
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        securityUser.setAuthorities(authorities);
        return securityUser;
        // @formatter:on
    }

}
