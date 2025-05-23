package top.ticho.starter.security.core.jwt;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的jwt扩展信息接口实现
 *
 * @author zhajianjun
 * @date 2022-09-23 10:47
 */
public class TiJwtExtra implements JwtExtra {

    @Override
    public Map<String, Object> getExtra() {
        Map<String, Object> extMap = new HashMap<>();
        extMap.put("author", "zhajianjun");
        return extMap;
    }

}
