package com.ticho.boot.security.filter;

import cn.hutool.core.convert.Convert;
import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-24 14:14:06
 */
public class JwtAuthenticationTokenFilter extends AbstractAuthTokenFilter<TichoSecurityProperty.User> {

    public JwtAuthenticationTokenFilter(JwtDecode jwtDecode, UserDetailsChecker userDetailsChecker) {
        super(jwtDecode, userDetailsChecker);
    }

    @Override
    public TichoSecurityProperty.User convert(Map<String, Object> decodeAndVerify) {
        String username = Optional.ofNullable(decodeAndVerify.get(OAuth2Const.USERNAME)).map(Object::toString).orElse(null);
        List<String> authorities = Optional.ofNullable(decodeAndVerify.get(OAuth2Const.AUTHORITIES)).map(x-> Convert.toList(String.class, x)).orElse(null);
        TichoSecurityProperty.User user = new TichoSecurityProperty.User();
        user.setUsername(username);
        user.setRoles(authorities);
        return user;
    }

}
