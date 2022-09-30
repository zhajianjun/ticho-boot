package com.ticho.boot.security.prop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.view.core.TichoSecurityUser;
import com.ticho.boot.web.util.SpringContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private List<TichoSecurityUser> users = new ArrayList<>();

    /** 权限过滤地址 */
    @Getter
    private String[] antPatterns = {"/health"};

    @Getter
    private List<AntPathRequestMatcher> antPathRequestMatchers = new ArrayList<>();

    public void setAntPatterns(String[] antPatterns) {
        // @formatter:off
        if (ArrayUtil.isEmpty(antPatterns)) {
            antPatterns = new String[]{"/health"};
        }
        this.antPatterns = antPatterns;
        this.antPathRequestMatchers = Arrays.stream(antPatterns).map(AntPathRequestMatcher::new).collect(Collectors.toList());
        // @formatter:on
    }

    public void setUsers(List<TichoSecurityUser> users) {
        // @formatter:off
        if (CollUtil.isEmpty(users)) {
            return;
        }
        PasswordEncoder passwordEncoder = SpringContext.getBean(PasswordEncoder.class);
        for (TichoSecurityUser userInfo : users) {
            String password = userInfo.getPassword();
            if (StrUtil.isBlank(password)) {
                continue;
            }
            userInfo.setPassword(passwordEncoder.encode(password));
        }
        this.users = users;
        // @formatter:on
    }

}
