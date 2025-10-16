package top.ticho.tool.core;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-08 23:21
 */
public class TiObjUtil {

    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (TiArrayUtil.isArray(object)) {
            return Array.getLength(object) == 0;
        }
        return switch (object) {
            case CharSequence cs -> cs.isEmpty();
            case Collection<?> collection -> collection.isEmpty();
            case Iterator<?> iterator -> !iterator.hasNext();
            case Map<?, ?> map -> map.isEmpty();
            case Optional<?> o -> o.isEmpty();
            default -> false;
        };
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 计算对象长度，如果是字符串调用其length函数，集合类调用其size函数，数组调用其length属性，其他可遍历对象遍历计算长度<br>
     * 支持的类型包括：
     * <ul>
     * <li>CharSequence</li>
     * <li>Map</li>
     * <li>Iterator</li>
     * <li>Enumeration</li>
     * <li>Array</li>
     * </ul>
     *
     * @param obj 被计算长度的对象
     * @return 长度
     */
    public static int length(Object obj) {
        switch (obj) {
            case null -> {
                return 0;
            }
            case CharSequence cs -> {
                return cs.length();
            }
            case Collection<?> collection -> {
                return collection.size();
            }
            case Map<?, ?> map -> {
                return map.size();
            }
            default -> {
            }
        }
        int count;
        if (obj instanceof Iterator<?> iter) {
            count = 0;
            while (iter.hasNext()) {
                count++;
                iter.next();
            }
            return count;
        }
        if (obj instanceof Enumeration<?> enumeration) {
            count = 0;
            while (enumeration.hasMoreElements()) {
                count++;
                enumeration.nextElement();
            }
            return count;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        return -1;
    }

    public static <T> T getIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }


}
