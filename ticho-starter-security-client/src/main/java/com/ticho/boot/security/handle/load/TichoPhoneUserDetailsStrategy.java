package com.ticho.boot.security.handle.load;

import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.dto.SecurityUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 根据手机号码查询用户信息
 *
 * @author zhajianjun
 * @date 2022-09-22 11:17
 */
@Component(SecurityConst.LOAD_USER_TYPE_PHONE)
@ConditionalOnMissingBean(name = SecurityConst.LOAD_USER_TYPE_PHONE)
public class TichoPhoneUserDetailsStrategy extends AbstractUserDetailsStrategy {

    @Override
    public SecurityUser loadUser(String account) {
        throw new UnsupportedOperationException("根据手机号码查询用户信息 服务未实现");
    }

}
