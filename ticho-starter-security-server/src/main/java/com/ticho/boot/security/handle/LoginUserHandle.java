package com.ticho.boot.security.handle;

import cn.hutool.core.collection.CollUtil;
import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.dto.LoginDto;
import com.ticho.boot.security.dto.OAuth2AccessToken;
import com.ticho.boot.security.handle.jwt.JwtConverter;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.security.handle.jwt.JwtEncode;
import com.ticho.boot.security.handle.jwt.JwtExtInfo;
import com.ticho.boot.security.handle.login.LoginUserStragety;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.exception.BizException;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-22 18:12
 */
@Component
public class LoginUserHandle {

    @Autowired
    private Map<String, LoginUserStragety> loginUserServiceMap;

    @Autowired
    private JwtEncode jwtEncode;

    @Autowired
    private JwtDecode jwtDecode;

    @Autowired
    private JwtConverter jwtConverter;

    @Autowired
    private Map<String, JwtExtInfo> jwtExtInfoMap;

    public OAuth2AccessToken token(LoginDto loginDto) {
        String account = loginDto.getUsername();
        String credentials = loginDto.getPassword();
        String type = loginDto.getType();
        // @formatter:off
        LoginUserStragety loadUserService = loginUserServiceMap.get(SecurityConst.LOGIN_USER_TYPE + type);
        if (loadUserService == null) {
            loadUserService = loginUserServiceMap.get(SecurityConst.LOGIN_USER_TYPE_USERNAME);
        }
        UserDetails securityUser = loadUserService.login(account, credentials);
        return getoAuth2AccessToken(securityUser);
        // @formatter:on
    }

    public OAuth2AccessToken refreshToken(String refreshToken) {
        Assert.isNotNull(refreshToken, BizErrCode.PARAM_ERROR, "参数不能为空");
        // @formatter:off
        Map<String, Object> decodeAndVerify = jwtDecode.decodeAndVerify(refreshToken);
        Object type = decodeAndVerify.getOrDefault(OAuth2Const.TYPE, "");
        Assert.isTrue(Objects.equals(type, OAuth2Const.REFRESH_TOKEN), BizErrCode.FAIL, "refreshToken不合法");
        String username = Optional.ofNullable(decodeAndVerify.get(OAuth2Const.USERNAME))
            .map(Object::toString)
            .orElseThrow(()-> new BizException(BizErrCode.FAIL, "用户名不存在"));
        UserDetailsService userDetailsService = SpringContext.getBean(SecurityConst.LOAD_USER_TYPE_USERNAME, UserDetailsService.class);
        UserDetails securityUser = userDetailsService.loadUserByUsername(username);
        return getoAuth2AccessToken(securityUser);
        // @formatter:on
    }

    public String getPublicKey() {
        return jwtConverter.getVerifierKey();
        // @formatter:on
    }

    private OAuth2AccessToken getoAuth2AccessToken(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest httpServletRequest = requestAttributes.getRequest();
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken();
        Map<String, Object> map = new HashMap<>(8);
        for (JwtExtInfo value : jwtExtInfoMap.values()) {
            Map<String, Object> ext = value.getExt();
            if (CollUtil.isEmpty(ext)) {
                continue;
            }
            map.putAll(ext);
        }
        oAuth2AccessToken.setExtInfo(map);
        jwtEncode.encode(oAuth2AccessToken, userDetails);
        return oAuth2AccessToken;
    }

}
