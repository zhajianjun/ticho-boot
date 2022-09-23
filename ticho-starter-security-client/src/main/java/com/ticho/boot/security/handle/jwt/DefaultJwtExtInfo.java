package com.ticho.boot.security.handle.jwt;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-23 10:47
 */
public class DefaultJwtExtInfo implements JwtExtInfo {

    @Override
    public Map<String, Object> getExt() {
        Map<String, Object> test = new HashMap<>();
        test.put("author", "zhajianjun");
        return test;
    }

}
