package com.ticho.boot.security.handle.jwt;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的jwt扩展信息接口实现
 *
 * @author zhajianjun
 * @date 2022-09-23 10:47
 */
public class TichoJwtExtInfo implements JwtExtInfo {

    @Override
    public Map<String, Object> getExt() {
        Map<String, Object> extMap = new HashMap<>();
        extMap.put("author", "zhajianjun");
        return extMap;
    }

}
