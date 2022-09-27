package com.ticho.boot.security.prop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.view.core.TichoSecurityUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class TichoSecurityProperty implements InitializingBean {

    /** 默认用户，如果UserDetailsService接口被实现则没有啥作用了 */
    @Getter
    private final List<TichoSecurityUser> users = new ArrayList<>();

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

    public void setUsers(List<TichoSecurityUser> tichoSecurityUsers) {
        // @formatter:off
        PasswordEncoder passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
        for (TichoSecurityUser userInfo : tichoSecurityUsers) {
            String password = userInfo.getPassword();
            if (StrUtil.isBlank(password)) {
                continue;
            }
            userInfo.setPassword(passwordEncoder.encode(password));
        }
        // @formatter:on
    }

    @Override
    public void afterPropertiesSet() {
        PasswordEncoder passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
        if (CollUtil.isNotEmpty(users)) {
            return;
        }
        TichoSecurityUser userInfo = new TichoSecurityUser();
        userInfo.setUsername(SecurityConst.DEFAULT_USERNAME);
        String password = IdUtil.fastUUID();
        userInfo.setPassword(passwordEncoder.encode(password));
        userInfo.setRoleIds(Collections.singletonList(SecurityConst.DEFAULT_ROLE));
        users.add(userInfo);
        log.info("默认用户信息：{}， 密码：{}", SecurityConst.DEFAULT_USERNAME, password);
    }

}
