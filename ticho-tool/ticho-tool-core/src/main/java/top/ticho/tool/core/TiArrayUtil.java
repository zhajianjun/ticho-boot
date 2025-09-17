package top.ticho.tool.core;

import org.apache.commons.lang3.ArrayUtils;

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
        return length(array) == 0;
    }

    public static boolean isNotEmpty(Object array) {
        return !isEmpty(array);
    }

    public static int length(Object array) {
        return array != null ? Array.getLength(array) : 0;
    }

    public static String toString(Object obj) {
        return ArrayUtils.toString(obj);
    }

    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

}
