package com.ticho.boot.security.handle.load;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import com.ticho.boot.view.core.TichoSecurityUser;
import com.ticho.boot.web.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
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
@Slf4j
public class TichoUsernameLoadUserService implements LoadUserService, InitializingBean {
    private TichoSecurityUser user = null;

    @Resource
    private TichoSecurityProperty tichoSecurityProperty;

    @Override
    public TichoSecurityUser load(String account) {
        // @formatter:off
        List<TichoSecurityUser> users = tichoSecurityProperty.getUsers();
        TichoSecurityUser tichoSecurityUser = users
            .stream()
            .filter(x-> Objects.equals(x.getUsername(), account))
            .findFirst()
            .orElse(null);
        // 拷贝一份对象进行返回，防止对源对象进行属性修改
        return JsonUtil.convert(tichoSecurityUser, TichoSecurityUser.class);
        // @formatter:on
    }


    @Override
    public void afterPropertiesSet() {
        List<TichoSecurityUser> users = tichoSecurityProperty.getUsers();
        if (CollUtil.isNotEmpty(users)) {
            return;
        }
        if (user != null) {
            users.add(user);
            return;
        }
        TichoSecurityUser userInfo = new TichoSecurityUser();
        userInfo.setUsername(SecurityConst.DEFAULT_USERNAME);
        String password = IdUtil.fastUUID();
        PasswordEncoder passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
        userInfo.setPassword(passwordEncoder.encode(password));
        userInfo.setRoleIds(Collections.singletonList(SecurityConst.DEFAULT_ROLE));
        users.add(userInfo);
        user = userInfo;
        log.info("默认用户信息：{}， 密码：{}", SecurityConst.DEFAULT_USERNAME, password);
    }

}
