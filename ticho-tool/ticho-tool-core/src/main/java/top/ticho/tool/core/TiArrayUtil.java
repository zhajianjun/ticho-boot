package top.ticho.tool.core;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 数组工具类
 * <p>提供数组判空、非空判断、获取长度以及转为字符串等方法</p>
 *
 * @author zhajianjun
 * @date 2025-08-17 14:21
 */
public class TiArrayUtil {

    /**
     * 判断泛型数组是否为空
     *
     * @param array 泛型数组
     * @param <T>   数组元素类型
     * @return 数组为 null 或长度为 0 时返回 true，否则返回 false
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断泛型数组是否非空
     *
     * @param array 泛型数组
     * @param <T>   数组元素类型
     * @return 数组不为 null 且长度大于 0 时返回 true，否则返回 false
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * 判断任意类型的数组是否为空（通过反射获取数组长度）
     *
     * @param array 任意类型的数组对象
     * @return 数组长度为 0 时返回 true，否则返回 false
     */
    public static boolean isEmpty(Object array) {
        return length(array) == 0;
    }

    /**
     * 判断任意类型的数组是否非空（通过反射获取数组长度）
     *
     * @param array 任意类型的数组对象
     * @return 数组长度大于 0 时返回 true，否则返回 false
     */
    public static boolean isNotEmpty(Object array) {
        return !isEmpty(array);
    }

    /**
     * 通过反射获取数组的长度
     *
     * @param array 任意类型的数组对象
     * @return 数组的长度；如果数组为 null 则返回 0
     */
    public static int length(Object array) {
        return array != null ? Array.getLength(array) : 0;
    }

    /**
     * 将数组转换为字符串表示
     * <p>支持所有基本类型数组和对象数组</p>
     *
     * @param obj 待转换的对象（可以是各种类型的数组）
     * @return 数组的字符串表示形式；如果对象为 null 则返回 null；如果不是数组则调用对象的 toString() 方法
     */
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

    /**
     * 判断对象是否为数组类型
     *
     * @param obj 待判断的对象
     * @return 对象不为 null 且是数组类型时返回 true，否则返回 false
     */
    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

}
