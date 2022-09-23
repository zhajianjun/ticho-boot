package com.ticho.boot.security.handle.load;


import com.ticho.boot.security.dto.SecurityUser;

/**
 * 查询用户策略
 *
 * @author zhajianjun
 * @date 2022-09-22 10:46
 */
public interface LoadUserStrategy {

    /**
     * 根据账户查询用户信息
     *
     * @param account 账户(用户名、手机号、密码等)
     * @return 用户信息
     */
    SecurityUser loadUser(String account);

}
