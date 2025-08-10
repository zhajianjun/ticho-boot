package top.ticho.starter.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.prop.TiSecurityProperty;
import top.ticho.starter.security.service.impl.AbstractLoginService;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.tool.core.TiCollUtil;
import top.ticho.tool.core.TiIdUtil;
import top.ticho.tool.json.util.TiJsonUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-04-20 13:40
 */
@Slf4j
public class TiLoginServiceImpl extends AbstractLoginService {
    private final TiSecurityProperty tiSecurityProperty;

    public TiLoginServiceImpl(TiSecurityProperty tiSecurityProperty, PasswordEncoder passwordEncoder) {
        this.tiSecurityProperty = tiSecurityProperty;
        List<TiSecurityUser> users = tiSecurityProperty.getUsers();
        if (TiCollUtil.isNotEmpty(users)) {
            return;
        }
        TiSecurityUser userInfo = new TiSecurityUser();
        userInfo.setUsername(TiSecurityConst.DEFAULT_USERNAME);
        String password = TiIdUtil.getUuid();
        userInfo.setPassword(passwordEncoder.encode(password));
        userInfo.setRoles(List.of(TiSecurityConst.DEFAULT_ROLE));
        users.add(userInfo);
        log.info("默认用户信息：{}， 密码：{}", TiSecurityConst.DEFAULT_USERNAME, password);
    }

    public TiSecurityUser load(String username) {
        List<TiSecurityUser> users = tiSecurityProperty.getUsers();
        TiSecurityUser tiSecurityUser = users
            .stream()
            .filter(x -> Objects.equals(x.getUsername(), username))
            .findFirst()
            .orElse(null);
        // 拷贝一份对象进行返回，防止对源对象进行属性修改
        return TiJsonUtil.copy(tiSecurityUser, TiSecurityUser.class);
    }

}
