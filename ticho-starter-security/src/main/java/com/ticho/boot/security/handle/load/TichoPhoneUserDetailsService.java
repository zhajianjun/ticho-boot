package com.ticho.boot.security.handle.load;

import com.ticho.boot.security.constant.SecurityConst;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 根据手机号码查询用户信息
 *
 * @author zhajianjun
 * @date 2022-09-22 11:17
 */
@Component(SecurityConst.LOAD_USER_TYPE_PHONE)
@ConditionalOnMissingBean(name = SecurityConst.LOAD_USER_TYPE_PHONE)
public class TichoPhoneUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String account) {
        throw new UnsupportedOperationException("根据手机号码查询用户信息 服务未实现");
    }

}
