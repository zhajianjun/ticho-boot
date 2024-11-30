package top.ticho.boot.security.filter;

import cn.hutool.core.convert.Convert;
import top.ticho.boot.security.constant.BaseSecurityConst;
import top.ticho.boot.view.core.BaseSecurityUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * jwt token验证过滤器
 *
 * @author zhajianjun
 * @date 2022-09-24 14:14:06
 */
public class BaseTokenAuthenticationTokenFilter extends AbstractAuthTokenFilter<BaseSecurityUser> {

    @Override
    public BaseSecurityUser convert(Map<String, Object> decodeAndVerify) {
        String username = Optional.ofNullable(decodeAndVerify.get(BaseSecurityConst.USERNAME)).map(Object::toString).orElse(null);
        List<String> authorities = Optional.ofNullable(decodeAndVerify.get(BaseSecurityConst.AUTHORITIES)).map(x -> Convert.toList(String.class, x)).orElse(null);
        BaseSecurityUser user = new BaseSecurityUser();
        user.setUsername(username);
        user.setRoles(authorities);
        return user;
    }

}
