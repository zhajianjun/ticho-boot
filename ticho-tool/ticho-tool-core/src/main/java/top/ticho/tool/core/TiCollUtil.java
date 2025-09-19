package top.ticho.tool.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:35
 */
public class TiCollUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

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

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
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

    public static <T> String join(Collection<T> collection, CharSequence conjunction) {
        if (null == collection) {
            return null;
        }
        return collection
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining(conjunction));
    }

}
