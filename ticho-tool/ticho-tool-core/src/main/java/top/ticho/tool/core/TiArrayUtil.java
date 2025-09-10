package top.ticho.tool.core;

import java.lang.reflect.Array;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-17 14:21
 */
public class TiArrayUtil {

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(Object array) {
        return getLength(array) == 0;
    }

    public static boolean isNotEmpty(Object array) {
        return !isEmpty(array);
    }

    public static int getLength(Object array) {
        return array != null ? Array.getLength(array) : 0;
    }

}
