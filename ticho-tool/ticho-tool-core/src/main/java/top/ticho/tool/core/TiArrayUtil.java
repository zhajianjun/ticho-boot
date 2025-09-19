package top.ticho.tool.core;

import java.lang.reflect.Array;
import java.util.Arrays;

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
        if (null == obj) {
            return null;
        }
        if (obj instanceof long[] array) {
            return Arrays.toString(array);
        } else if (obj instanceof int[] array) {
            return Arrays.toString(array);
        } else if (obj instanceof short[] array) {
            return Arrays.toString(array);
        } else if (obj instanceof char[] array) {
            return Arrays.toString(array);
        } else if (obj instanceof byte[] array) {
            return Arrays.toString(array);
        } else if (obj instanceof boolean[] array) {
            return Arrays.toString(array);
        } else if (obj instanceof float[] array) {
            return Arrays.toString(array);
        } else if (obj instanceof double[] array) {
            return Arrays.toString(array);
        } else if (TiArrayUtil.isArray(obj)) {
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
            }
        }
        return obj.toString();
    }

    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

}
