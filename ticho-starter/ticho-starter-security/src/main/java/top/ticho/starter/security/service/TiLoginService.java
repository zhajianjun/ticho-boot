package top.ticho.starter.security.service;

import top.ticho.starter.security.dto.LoginRequest;
import top.ticho.starter.security.dto.TiToken;
import top.ticho.starter.view.core.TiSecurityUser;

/**
 * 登录处理接口
 *
 * @author zhajianjun
 * @date 2022-09-22 18:12
 */
public interface TiLoginService {

    /**
     * 查询用户信息
     *
     * @param username 用户名
     * @return {@link TiSecurityUser }
     */
    TiSecurityUser load(String username);

    /**
     * 登录获取token
     *
     * @param loginRequest 登录请求参数
     * @return token信息
     */
    TiToken token(LoginRequest loginRequest);

    /**
     * refresh token 刷新token
     *
     * @param refreshToken refresh token
     * @return token信息
     */
    TiToken refreshToken(String refreshToken);

    /**
     * 获取公钥
     *
     * @return 获取公钥
     */
    String publicKey();

}
