package top.ticho.tool.core;

import java.util.Map;

/**
 * Map 工具类
 * <p>提供 Map 集合的常用判断方法</p>
 *
 * @author zhajianjun
 * @date 2025-08-10 10:44
 */
public class TiMapUtil {

    /**
     * 判断 Map 是否为空
     * <p>当 Map 为 null 或者 Map 中不包含任何元素时返回 true</p>
     *
     * @param map 待判断的 Map 对象
     * @return 如果 Map 为空则返回 true，否则返回 false
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断 Map 是否不为空
     * <p>当 Map 不为 null 并且 Map 中包含至少一个元素时返回 true</p>
     *
     * @param map 待判断的 Map 对象
     * @return 如果 Map 不为空则返回 true，否则返回 false
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
