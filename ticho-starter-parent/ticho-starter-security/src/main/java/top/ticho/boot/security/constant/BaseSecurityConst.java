package top.ticho.boot.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * security相关静态常量
 *
 * @author zhajianjun
 * @date 2022-09-22 11:07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseSecurityConst {

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


    /** 加载用户类型: */
    public static final String LOAD_USER_TYPE = "load_user_type:";

    /** 登录方式: */
    public static final String LOGIN_USER_TYPE = "login_user_type:";

    /** 用户密码 */
    public static final String USERNAME = "username";
    /** 手机号码 */
    public static final String EMAIL = "email";
    /** 邮箱 */
    public static final String PHONE = "phone";

    /** 加载用户类型:用户密码 */
    public static final String LOAD_USER_TYPE_USERNAME = LOAD_USER_TYPE + USERNAME;
    /** 加载用户类型:手机号码 */
    public static final String LOAD_USER_TYPE_PHONE = LOAD_USER_TYPE + EMAIL;
    /** 加载用户类型:邮箱 */
    public static final String LOAD_USER_TYPE_EMAIL = LOAD_USER_TYPE + PHONE;


    /** 登录方式:用户密码 */
    public static final String LOGIN_USER_TYPE_USERNAME = LOGIN_USER_TYPE + USERNAME;
    /** 登录方式:手机号码 */
    public static final String LOGIN_USER_TYPE_PHONE = LOGIN_USER_TYPE + EMAIL;
    /** 登录方式:邮箱 */
    public static final String LOGIN_USER_TYPE_EMAIL = LOGIN_USER_TYPE + PHONE;


}
