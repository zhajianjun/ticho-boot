package top.ticho.starter.security.handle;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.ticho.starter.security.dto.Oauth2AccessToken;
import top.ticho.starter.security.handle.jwt.JwtEncode;
import top.ticho.starter.security.handle.jwt.JwtExtra;
import top.ticho.starter.security.handle.jwt.JwtSigner;
import top.ticho.boot.view.core.TiSecurityUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 登录处理抽象类
 * <p> 抽取公共方法 </p>
 *
 * @author zhajianjun
 * @date 2022-09-24 16:29:51
 */
public abstract class AbstractLoginUserHandle implements LoginUserHandle {

    @Autowired
    private JwtEncode jwtEncode;

    @Autowired
    private JwtSigner jwtSigner;

    @Autowired
    private Map<String, JwtExtra> jwtExtInfoMap;


    public String publicKey() {
        return jwtSigner.getVerifierKey();
    }

    /**
     * 根据权限用户获取token返回信息
     *
     * @param tiSecurityUser 权限用户
     * @return token返回信息
     */
    protected Oauth2AccessToken getOauth2TokenAndSetAuthentication(TiSecurityUser tiSecurityUser) {
        List<String> authoritieStrs = Optional.ofNullable(tiSecurityUser.getRoles()).orElseGet(ArrayList::new);
        tiSecurityUser.setPassword("N/A");
        List<SimpleGrantedAuthority> authorities = authoritieStrs.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tiSecurityUser, tiSecurityUser.getPassword(), authorities);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest httpServletRequest = requestAttributes.getRequest();
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Oauth2AccessToken oAuth2AccessToken = new Oauth2AccessToken();
        Map<String, Object> map = getAllJwtExtInfo();
        // jwt扩展接口的所有数据放入token中
        oAuth2AccessToken.setExtInfo(map);
        jwtEncode.encode(oAuth2AccessToken, tiSecurityUser);
        return oAuth2AccessToken;
    }

    /**
     * 获取jwt扩展接口的所有数据
     */
    private Map<String, Object> getAllJwtExtInfo() {
        Map<String, Object> map = new HashMap<>(8);
        for (JwtExtra value : jwtExtInfoMap.values()) {
            Map<String, Object> ext = value.getExtra();
            if (CollUtil.isEmpty(ext)) {
                continue;
            }
            map.putAll(ext);
        }
        return map;
    }
}
