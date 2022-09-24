package com.ticho.boot.security.handle;

import cn.hutool.core.collection.CollUtil;
import com.ticho.boot.security.dto.Oauth2AccessToken;
import com.ticho.boot.security.handle.jwt.JwtConverter;
import com.ticho.boot.security.handle.jwt.JwtEncode;
import com.ticho.boot.security.handle.jwt.JwtExtInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
    private JwtConverter jwtConverter;

    @Autowired
    private Map<String, JwtExtInfo> jwtExtInfoMap;


    public String getPublicKey() {
        return jwtConverter.getVerifierKey();
        // @formatter:on
    }

    /**
     * 根据权限用户获取token返回信息
     *
     * @param userDetails 权限用户
     * @return token返回信息
     */
    protected Oauth2AccessToken getOauth2TokenAndSetAuthentication(UserDetails userDetails) {
        // @formatter:off
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
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
        jwtEncode.encode(oAuth2AccessToken, userDetails);
        return oAuth2AccessToken;
        // @formatter:on
    }

    /**
     * 获取jwt扩展接口的所有数据
     */
    private Map<String, Object> getAllJwtExtInfo() {
        Map<String, Object> map = new HashMap<>(8);
        for (JwtExtInfo value : jwtExtInfoMap.values()) {
            Map<String, Object> ext = value.getExt();
            if (CollUtil.isEmpty(ext)) {
                continue;
            }
            map.putAll(ext);
        }
        return map;
    }
}
