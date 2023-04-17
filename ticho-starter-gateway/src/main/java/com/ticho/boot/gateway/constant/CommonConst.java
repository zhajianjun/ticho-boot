package com.ticho.boot.gateway.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * oauth2相关静态常量
 *
 * @author zhajianjun
 * @date 2022-09-21 16:59
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConst {

    public static final String HEADER_NAME = "X-Forwarded-Prefix";

    public static final String URI = "/v2/api-docs";

    public static final String INNER = "inner";

    public static final String INNER_VALUE_FALSE = "false";

}
