package top.ticho.starter.security.handle.load;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.prop.TiSecurityProperty;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.tool.json.util.TiJsonUtil;

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
public class TiLoadUserService implements LoadUserService, InitializingBean {
    private TiSecurityUser user = null;

    @Resource
    private TiSecurityProperty tiSecurityProperty;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public TiSecurityUser load(String account) {
        List<TiSecurityUser> users = tiSecurityProperty.getUsers();
        TiSecurityUser tiSecurityUser = users
            .stream()
            .filter(x -> Objects.equals(x.getUsername(), account))
            .findFirst()
            .orElse(null);
        // 拷贝一份对象进行返回，防止对源对象进行属性修改
        return TiJsonUtil.copy(tiSecurityUser, TiSecurityUser.class);
    }


    @Override
    public void afterPropertiesSet() {
        List<TiSecurityUser> users = tiSecurityProperty.getUsers();
        if (CollUtil.isNotEmpty(users)) {
            return;
        }
        if (user != null) {
            users.add(user);
            return;
        }
        TiSecurityUser userInfo = new TiSecurityUser();
        userInfo.setUsername(TiSecurityConst.DEFAULT_USERNAME);
        String password = IdUtil.fastUUID();
        userInfo.setPassword(passwordEncoder.encode(password));
        userInfo.setRoles(Collections.singletonList(TiSecurityConst.DEFAULT_ROLE));
        users.add(userInfo);
        user = userInfo;
        log.info("默认用户信息：{}， 密码：{}", TiSecurityConst.DEFAULT_USERNAME, password);
    }

}
