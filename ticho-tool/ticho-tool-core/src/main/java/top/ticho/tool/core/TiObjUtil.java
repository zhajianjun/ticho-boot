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

    /**
     * 判断对象是否为空
     * <p>支持以下类型的判空：
     * <ul>
     * <li>{@code null} - 返回 true</li>
     * <li>数组 - 长度为 0 时返回 true</li>
     * <li>{@link CharSequence} - 调用 {@code isEmpty()} 方法</li>
     * <li>{@link Collection} - 调用 {@code isEmpty()} 方法</li>
     * <li>{@link Iterator} - 没有下一个元素时返回 true</li>
     * <li>{@link Map} - 调用 {@code isEmpty()} 方法</li>
     * <li>{@link Optional} - 调用 {@code isEmpty()} 方法</li>
     * </ul>
     *
     * @param object 待判断的对象
     * @return 如果对象为空返回 true，否则返回 false
     */
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

    /**
     * 判断对象是否不为空
     *
     * @param obj 待判断的对象
     * @return 如果对象不为空返回 true，否则返回 false
     * @see #isEmpty(Object)
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 计算对象长度
     * <p>支持以下类型的长度计算：
     * <ul>
     * <li>{@code null} - 返回 0</li>
     * <li>{@link CharSequence} - 调用 {@code length()} 方法</li>
     * <li>{@link Collection} - 调用 {@code size()} 方法</li>
     * <li>{@link Map} - 调用 {@code size()} 方法</li>
     * <li>{@link Iterator} - 遍历迭代器计算元素个数</li>
     * <li>{@link Enumeration} - 遍历枚举计算元素个数</li>
     * <li>数组 - 获取数组长度</li>
     * </ul>
     * <p>注意：对于 {@link Iterator} 和 {@link Enumeration} 类型，遍历会消耗其中的元素</p>
     *
     * @param obj 被计算长度的对象
     * @return 长度值，如果对象类型不支持则返回 -1
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

    /**
     * 如果对象为 null 则返回默认值，否则返回对象本身
     *
     * @param <T>          对象类型
     * @param object       待判断的对象
     * @param defaultValue 默认值
     * @return 如果对象为 null 返回默认值，否则返回对象本身
     */
    public static <T> T getIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }


}
