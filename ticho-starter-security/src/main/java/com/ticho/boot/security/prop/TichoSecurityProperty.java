package com.ticho.boot.security.prop;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
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
@NoArgsConstructor
@Slf4j
public class TichoSecurityProperty {

    /** 默认用户，如果UserDetailsService接口被实现则没有啥作用了 */
    @Getter
    @Setter
    private List<User> users;

    /** 权限过滤地址 */
    @Getter
    private String[] antPatterns = {"/health"};

    @Getter
    private List<AntPathRequestMatcher> antPathRequestMatchers;

    public void setAntPatterns(String[] antPatterns) {
        // @formatter:off
        if (ArrayUtil.isEmpty(antPatterns)) {
            antPatterns = new String[]{"/health"};
        }
        this.antPatterns = antPatterns;
        this.antPathRequestMatchers = Arrays.stream(antPatterns).map(AntPathRequestMatcher::new).collect(Collectors.toList());
        // @formatter:on
    }

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
}
