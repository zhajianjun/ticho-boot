package top.ticho.tool.core;

import org.apache.commons.lang3.ClassUtils;
import top.ticho.tool.core.exception.TiUtilException;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-09 20:07
 */
public class TiClassUtil {

    public static boolean isSimpleValueType(Class<?> aClass) {
        return ClassUtils.isPrimitiveOrWrapper(aClass);
    }

    public static <T> Class<T> loadClass(String className) {
        Class<?> aClass;
        try {
            aClass = ClassUtils.getClass(className);
        } catch (ClassNotFoundException e) {
            throw new TiUtilException(e);
        }
        return (Class<T>) aClass;
    }

}
