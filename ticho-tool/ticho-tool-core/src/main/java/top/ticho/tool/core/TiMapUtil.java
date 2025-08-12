package top.ticho.tool.core;

import cn.hutool.core.map.MapUtil;

import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 10:44
 */
public class TiMapUtil {

    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtil.isEmpty(map);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtil.isNotEmpty(map);
    }

}
