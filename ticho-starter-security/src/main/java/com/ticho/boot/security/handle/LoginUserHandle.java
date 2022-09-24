package com.ticho.boot.security.handle;

import com.ticho.boot.security.dto.LoginRequest;
import com.ticho.boot.security.dto.Oauth2AccessToken;
import org.springframework.stereotype.Component;

/**
 * 登录处理接口
 *
 * @author zhajianjun
 * @date 2022-09-22 18:12
 */
@Component
public interface LoginUserHandle {

    /**
     * 登录获取token
     *
     * @param loginRequest 登录请求参数
     * @return token信息
     */
    Oauth2AccessToken token(LoginRequest loginRequest);

    /**
     * refresh token 刷新token
     *
     * @param refreshToken refresh token
     * @return token信息
     */
    Oauth2AccessToken refreshToken(String refreshToken);

    /**
     * 获取公钥
     *
     * @return 获取公钥
     */
    String getPublicKey();


}
