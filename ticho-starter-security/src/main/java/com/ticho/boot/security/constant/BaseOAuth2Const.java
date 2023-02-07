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
public class BaseOAuth2Const {

    public static final String OAUTH2_TOKEN_FILTER_BEAN_NAME = "oauth2_token_filter_bean_name";

    public static final String OAUTH2_CONTROLLER = "oauth2_controller";

}
