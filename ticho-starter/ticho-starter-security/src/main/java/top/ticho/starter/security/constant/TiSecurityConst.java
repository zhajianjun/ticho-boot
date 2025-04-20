package top.ticho.starter.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * security相关静态常量
 *
 * @author zhajianjun
 * @date 2022-09-22 11:07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiSecurityConst {

    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_ROLE = "admin";

    public static final String AUTHORITIES = "authorities";

    public static final String EXP = "exp";

    public static final String BEARER = "Bearer";

    public static final String ACCESS_TOKEN = "access_token";

    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String EXPIRES_IN = "expires_in";

    public static final String IAT = "iat";

    public static final String TYPE = "type";

    public static final String TOKEN_TYPE = "token_type";

    public static final String PM = "pm";

    /** 用户密码 */
    public static final String USERNAME = "username";

    public static final String OAUTH2_CONTROLLER = "oauth2_controller";

    public static final String INNER = "inner";

    public static final String INNER_VALUE = "true";


}
