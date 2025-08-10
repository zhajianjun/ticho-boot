package top.ticho.tool.core;

import cn.hutool.core.collection.CollUtil;

import java.util.Collection;
import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:35
 */
public class TiCollUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return CollUtil.isEmpty(collection);
    }

    public static <T> List<List<T>> split(Collection<T> collection, int batchSize) {
        return CollUtil.split(collection, batchSize);
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return CollUtil.isNotEmpty(collection);
    }

    public static List<String> toList(Collection<?> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection
            .stream()
            .map(Object::toString)
            .toList();
    }

}
