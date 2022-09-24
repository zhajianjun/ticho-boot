package com.ticho.boot.security.prop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ticho.boot.security.constant.SecurityConst;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * security参数配置对象
 *
 * @author zhajianjun
 * @date 2022-09-23 14:46
 */
@Data
@Slf4j
public class TichoSecurityProperty implements InitializingBean {

    /** 默认用户，如果UserDetailsService接口被实现则没有啥作用了 */
    private List<User> user;

    /** 权限过滤地址 */
    private String[] antPatterns = {"/health"};

    /**
     * 临时用户对象
     */
    @Data
    public static class User implements UserDetails {

        /** 用户名 */
        private String username;
        /** 密码 */
        private String password;
        /** 角色 */
        private List<String> roles;

        public void setPassword(String password) {
            if (password == null) {
                return;
            }
            PasswordEncoder passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
            this.password = passwordEncoder.encode(password);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // @formatter:off
            return Optional.ofNullable(roles).orElseGet(Collections::emptyList)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            // @formatter:on
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (CollUtil.isNotEmpty(user)) {
            return;
        }
        user = new ArrayList<>();
        TichoSecurityProperty.User userInfo = new TichoSecurityProperty.User();
        userInfo.setUsername(SecurityConst.DEFAULT_USERNAME);
        String password = IdUtil.fastUUID();
        userInfo.setPassword(password);
        userInfo.setRoles(Collections.singletonList(SecurityConst.DEFAULT_ROLE));
        user.add(userInfo);
        log.info("默认用户信息：{}， 密码：{}", SecurityConst.DEFAULT_USERNAME, password);
    }

}
