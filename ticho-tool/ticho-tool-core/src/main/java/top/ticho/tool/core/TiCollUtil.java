package top.ticho.tool.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合工具类
 * <p>提供集合判空、分割、转换、拼接等常用操作</p>
 *
 * @author zhajianjun
 * @date 2025-08-04 22:35
 */
public class TiCollUtil {

    /**
     * 判断集合是否为空
     * <p>当集合为 null 或大小为 0 时返回 true</p>
     *
     * @param collection 待判断的集合
     * @return 集合为空返回 true，否则返回 false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 将集合按指定批次大小分割
     * <p>例如：100 条数据，每批 20 条，可分成 5 批处理</p>
     *
     * @param collection 待分割的集合
     * @param batchSize  每批的数量
     * @param <T>        集合元素类型
     * @return 分割后的二维列表，每个子列表最多包含 batchSize 个元素
     */
    public static <T> List<List<T>> split(Collection<T> collection, int batchSize) {
        final List<List<T>> result = new ArrayList<>();
        if (isEmpty(collection)) {
            return result;
        }
        final int initSize = Math.min(collection.size(), batchSize);
        List<T> subList = new ArrayList<>(initSize);
        for (T t : collection) {
            if (subList.size() >= batchSize) {
                result.add(subList);
                subList = new ArrayList<>(initSize);
            }
            subList.add(t);
        }
        result.add(subList);
        return result;
    }

    /**
     * 判断集合是否非空
     * <p>当集合不为 null 且大小大于 0 时返回 true</p>
     *
     * @param collection 待判断的集合
     * @return 集合非空返回 true，否则返回 false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 将集合转换为字符串列表
     * <p>调用每个元素的 toString() 方法进行转换</p>
     *
     * @param collection 待转换的集合
     * @return 字符串列表，若集合为空则返回 null
     */
    public static List<String> toList(Collection<?> collection) {
        return toList(collection, Object::toString);
    }

    /**
     * 将集合按指定转换函数转换为另一种类型的列表
     *
     * @param collection 待转换的集合
     * @param function   转换函数
     * @param <T>        原始元素类型
     * @param <R>        目标元素类型
     * @return 转换后的列表，若集合为空则返回 null
     */
    public static <T, R> List<R> toList(Collection<T> collection, Function<T, R> function) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection
            .stream()
            .map(function)
            .toList();
    }

    /**
     * 将集合元素用分隔符拼接成字符串
     * <p>调用每个元素的 toString() 方法进行转换</p>
     *
     * @param collection 待拼接的集合
     * @param delimiter  分隔符
     * @param <T>        集合元素类型
     * @return 拼接后的字符串，若集合为 null 则返回 null
     */
    public static <T> String join(Collection<T> collection, CharSequence delimiter) {
        if (null == collection) {
            return null;
        }
        return collection
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining(delimiter));
    }

    /**
     * 将集合元素用分隔符拼接成字符串，可添加前后缀
     * <p>调用每个元素的 toString() 方法进行转换</p>
     *
     * @param collection 待拼接的集合
     * @param delimiter  分隔符
     * @param prefix     前缀
     * @param suffix     后缀
     * @param <T>        集合元素类型
     * @return 拼接后的字符串，若集合为 null 则返回 null
     */
    public static <T> String join(Collection<T> collection, CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        if (null == collection) {
            return null;
        }
        return collection
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining(delimiter, prefix, suffix));
    }

}
