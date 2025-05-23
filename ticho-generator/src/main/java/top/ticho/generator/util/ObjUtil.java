package top.ticho.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjUtil {

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional<?> optional) {
            return optional.isEmpty();
        } else if (obj instanceof CharSequence charSequence) {
            return charSequence.isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection<?> collection) {
            return collection.isEmpty();
        } else {
            return obj instanceof Map<?, ?> map && map.isEmpty();
        }
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

}
