package top.ticho.tool.core;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 10:44
 */
public class TiMapUtil {

    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtils.isNotEmpty(map);
    }

}
