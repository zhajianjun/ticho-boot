package top.ticho.boot.security.handle.load;

import top.ticho.boot.view.core.BaseSecurityUser;

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
    BaseSecurityUser load(String account);

}
