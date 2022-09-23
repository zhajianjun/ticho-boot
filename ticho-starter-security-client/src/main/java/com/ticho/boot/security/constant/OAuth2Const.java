package com.ticho.boot.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * oauth2相关静态常量
 *
 * @author zhajianjun
 * @date 2022-09-21 16:59
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2Const {

    public static final String BEARER_TYPE = "Bearer";

    public static final String ACCESS_TOKEN = "access_token";

    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String USERNAME = "username";

    public static final String AUTHORITIES = "authorities";

    public static final String EXP = "exp";

    public static final String EXPIRES_IN = "expires_in";

    public static final String IAT = "iat";

    public static final String TYPE = "type";

    public static final String STATUS = "status";
}
