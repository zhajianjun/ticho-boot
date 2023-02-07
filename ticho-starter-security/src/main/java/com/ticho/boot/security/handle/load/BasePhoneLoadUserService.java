package com.ticho.boot.security.handle.load;

import com.ticho.boot.security.constant.BaseSecurityConst;
import com.ticho.boot.view.core.BaseSecurityUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 根据手机号码查询用户信息
 *
 * @author zhajianjun
 * @date 2022-09-22 11:17
 */
@Component(BaseSecurityConst.LOAD_USER_TYPE_PHONE)
@ConditionalOnMissingBean(name = BaseSecurityConst.LOAD_USER_TYPE_PHONE)
public class BasePhoneLoadUserService implements LoadUserService {

    @Override
    public BaseSecurityUser load(String account) {
        throw new UnsupportedOperationException("根据手机号码查询用户信息 服务未实现");
    }

}
