package com.ticho.boot.security.handle.load;

import com.ticho.boot.view.core.TichoSecurityUser;

/**
 * 用户查询服务
 *
 * @author zhajianjun
 * @date 2022-09-26 08:41
 */
public interface LoadUserService {

    /**
     * 根据凭证获取token信息
     *
     * @param account 账户
     * @return 用户信息
     */
    TichoSecurityUser load(String account);

}
