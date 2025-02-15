package top.ticho.starter.security.handle.load;

import top.ticho.starter.view.core.TiSecurityUser;

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
    TiSecurityUser load(String account);

}
