package com.ticho.boot.security.handle.load;

import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import com.ticho.boot.view.core.TichoSecurityUser;
import com.ticho.boot.web.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@Slf4j
public class TichoUsernameLoadUserService implements LoadUserService {

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

}
