package top.ticho.tool.core;

import cn.hutool.core.util.ArrayUtil;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-17 14:21
 */
public class TiArrayUtil {

    public static <T> boolean isEmpty(T[] array) {
        return ArrayUtil.isEmpty(array);
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return ArrayUtil.isNotEmpty(array);
    }

}
