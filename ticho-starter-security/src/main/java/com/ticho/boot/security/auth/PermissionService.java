package com.ticho.boot.security.auth;

/**
 * 接口权限
 *
 * @author zhajianjun
 * @date 2022-09-26 17:31:58
 */
public interface PermissionService {

    /**
     * 判断接口是否有任意xxx，xxx权限
     * @param permissions 权限
     * @return {boolean}
     */
    boolean hasPerms(String... permissions);

}
