package top.ticho.starter.transform.serializer;


import top.ticho.starter.transform.annotation.TiRendering;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2025-03-21 21:44
 */
public class TiSerializerFactory {
    private static final Map<Class<?>, TiRendering<?, ?>> map = new ConcurrentHashMap<>();

    public static TiRendering<?, ?> getSerializer(Class<? extends TiRendering<?, ?>> clazz) {
        if (clazz.isInterface()) {
            throw new UnsupportedOperationException("Serializer is interface, what is expected is an implementation class !");
        }
        return map.computeIfAbsent(
            clazz,
            key -> {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new UnsupportedOperationException(e.getMessage(), e);
                }
            }
        );
    }

}
