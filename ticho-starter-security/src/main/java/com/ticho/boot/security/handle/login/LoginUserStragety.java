package com.ticho.boot.security.handle.login;


import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户登录策略
 *
 * @author zhajianjun
 * @date 2022-09-22 10:46
 */
public interface LoginUserStragety {

    /**
     * 根据凭证获取token信息
     *
     * @param account 账户
     * @param credentials 凭证
     * @return 用户信息
     */
    UserDetails login(String account, String credentials);

}