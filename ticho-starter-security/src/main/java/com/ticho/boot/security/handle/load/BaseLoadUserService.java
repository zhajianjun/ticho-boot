package com.ticho.boot.security.handle.load;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.security.constant.BaseSecurityConst;
import com.ticho.boot.security.prop.BaseSecurityProperty;
import com.ticho.boot.view.core.BaseSecurityUser;
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
@Component
@ConditionalOnMissingBean(LoadUserService.class)
@Slf4j
public class BaseLoadUserService implements LoadUserService, InitializingBean {
    private BaseSecurityUser user = null;

    @Resource
    private BaseSecurityProperty baseSecurityProperty;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public BaseSecurityUser load(String account) {
        // @formatter:off
        List<BaseSecurityUser> users = baseSecurityProperty.getUsers();
        BaseSecurityUser baseSecurityUser = users
            .stream()
            .filter(x-> Objects.equals(x.getUsername(), account))
            .findFirst()
            .orElse(null);
        // 拷贝一份对象进行返回，防止对源对象进行属性修改
        return JsonUtil.convert(baseSecurityUser, BaseSecurityUser.class);
        // @formatter:on
    }


    @Override
    public void afterPropertiesSet() {
        List<BaseSecurityUser> users = baseSecurityProperty.getUsers();
        if (CollUtil.isNotEmpty(users)) {
            return;
        }
        if (user != null) {
            users.add(user);
            return;
        }
        BaseSecurityUser userInfo = new BaseSecurityUser();
        userInfo.setUsername(BaseSecurityConst.DEFAULT_USERNAME);
        String password = IdUtil.fastUUID();
        userInfo.setPassword(passwordEncoder.encode(password));
        userInfo.setRoles(Collections.singletonList(BaseSecurityConst.DEFAULT_ROLE));
        users.add(userInfo);
        user = userInfo;
        log.info("默认用户信息：{}， 密码：{}", BaseSecurityConst.DEFAULT_USERNAME, password);
    }

}
