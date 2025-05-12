package top.ticho.starter.security.filter;

import cn.hutool.core.convert.Convert;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.view.core.TiSecurityUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * jwt token验证过滤器
 *
 * @author zhajianjun
 * @date 2022-09-24 14:14
 */
public class TiTokenAuthenticationTokenFilter extends AbstractAuthTokenFilter<TiSecurityUser> {

    @Override
    public TiSecurityUser convert(Map<String, Object> decodeAndVerify) {
        String username = Optional.ofNullable(decodeAndVerify.get(TiSecurityConst.USERNAME)).map(Object::toString).orElse(null);
        List<String> authorities = Optional.ofNullable(decodeAndVerify.get(TiSecurityConst.AUTHORITIES)).map(x -> Convert.toList(String.class, x)).orElse(null);
        TiSecurityUser user = new TiSecurityUser();
        user.setUsername(username);
        user.setRoles(authorities);
        return user;
    }

}
