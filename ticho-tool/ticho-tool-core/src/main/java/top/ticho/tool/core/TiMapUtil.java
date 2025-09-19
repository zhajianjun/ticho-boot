package top.ticho.tool.core;

import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 10:44
 */
public class TiMapUtil {

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
