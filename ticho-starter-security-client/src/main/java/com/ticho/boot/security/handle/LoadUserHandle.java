package com.ticho.boot.security.handle;

import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.dto.SecurityUser;
import com.ticho.boot.security.handle.load.LoadUserStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户查询统一处理
 *
 * @author zhajianjun
 * @date 2022-09-22 10:44
 */
@Component
public class LoadUserHandle {

    @Autowired
    private Map<String, LoadUserStrategy> detailsServiceMap;

    public SecurityUser loadUser(String account, String type) {
        LoadUserStrategy loadUserStrategy = detailsServiceMap.get(type);
        if (loadUserStrategy == null) {
            loadUserStrategy = detailsServiceMap.get(SecurityConst.LOAD_USER_TYPE_USERNAME);
        }
        return loadUserStrategy.loadUser(account);
    }
}
