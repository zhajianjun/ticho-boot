package com.ticho.boot.security.handle;

import cn.hutool.core.util.StrUtil;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.dto.LoginRequest;
import com.ticho.boot.security.dto.Oauth2AccessToken;
import com.ticho.boot.security.handle.jwt.JwtSigner;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.security.handle.load.LoadUserService;
import com.ticho.boot.security.handle.login.LoginUserStragety;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.TichoSecurityUser;
import com.ticho.boot.view.exception.BizException;
import com.ticho.boot.view.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 默认登录处理接口实现
 *
 * @author zhajianjun
 * @date 2022-09-24 16:29:51
 */
@Component
public class TichoLoginUserHandle extends AbstractLoginUserHandle {

    @Autowired
    private Map<String, LoginUserStragety> loginUserServiceMap;

    @Autowired
    private Map<String, LoadUserService> loadUserServiceMap;

    @Autowired
    private JwtDecode jwtDecode;

    @Autowired
    private JwtSigner jwtSigner;

    public Oauth2AccessToken token(LoginRequest loginRequest) {
        String account = loginRequest.getUsername();
        String credentials = loginRequest.getPassword();
        String type = loginRequest.getType();
        // 默认用户密码登录方式
        if (StrUtil.isBlank(type)) {
            type = SecurityConst.USERNAME;
        }
        // @formatter:off
        LoginUserStragety loadUserService = loginUserServiceMap.get(SecurityConst.LOGIN_USER_TYPE + type);
        Assert.isNotNull(loadUserService, BizErrCode.FAIL, "不存在的登录方式");
        TichoSecurityUser tichoSecurityUser = loadUserService.login(account, credentials);
        return getOauth2TokenAndSetAuthentication(tichoSecurityUser);
        // @formatter:on
    }

    public Oauth2AccessToken refreshToken(String refreshToken) {
        Assert.isNotNull(refreshToken, BizErrCode.PARAM_ERROR, "参数不能为空");
        // @formatter:off
        Map<String, Object> decodeAndVerify = jwtDecode.decodeAndVerify(refreshToken);
        Object type = decodeAndVerify.getOrDefault(SecurityConst.TYPE, "");
        Assert.isTrue(Objects.equals(type, SecurityConst.REFRESH_TOKEN), BizErrCode.FAIL, "refreshToken不合法");
        String username = Optional.ofNullable(decodeAndVerify.get(SecurityConst.USERNAME))
            .map(Object::toString)
            .orElseThrow(()-> new BizException(BizErrCode.FAIL, "用户名不存在"));
        LoadUserService loadUserService = loadUserServiceMap.get(SecurityConst.LOAD_USER_TYPE_USERNAME);
        TichoSecurityUser tichoSecurityUser = loadUserService.load(username);
        return getOauth2TokenAndSetAuthentication(tichoSecurityUser);
        // @formatter:on
    }

    @Override
    public String publicKey() {
        return jwtSigner.getVerifierKey();
        // @formatter:on
    }
}
