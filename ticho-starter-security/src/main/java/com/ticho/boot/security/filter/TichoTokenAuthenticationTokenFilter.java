package com.ticho.boot.security.filter;

import cn.hutool.core.convert.Convert;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.view.core.TichoSecurityUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * jwt token验证过滤器
 *
 * @author zhajianjun
 * @date 2022-09-24 14:14:06
 */
public class TichoTokenAuthenticationTokenFilter extends AbstractAuthTokenFilter<TichoSecurityUser> {

    @Override
    public TichoSecurityUser convert(Map<String, Object> decodeAndVerify) {
        // @formatter:off
        String username = Optional.ofNullable(decodeAndVerify.get(SecurityConst.USERNAME)).map(Object::toString).orElse(null);
        List<String> authorities = Optional.ofNullable(decodeAndVerify.get(SecurityConst.AUTHORITIES)).map(x-> Convert.toList(String.class, x)).orElse(null);
        TichoSecurityUser user = new TichoSecurityUser();
        user.setUsername(username);
        user.setRoleIds(authorities);
        return user;
        // @formatter:on
    }

}
