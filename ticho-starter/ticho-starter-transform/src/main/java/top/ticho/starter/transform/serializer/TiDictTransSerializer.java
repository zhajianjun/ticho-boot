package top.ticho.starter.transform.serializer;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Setter;
import top.ticho.starter.cache.component.TiCacheTemplate;
import top.ticho.starter.transform.annotation.TiDictTrans;
import top.ticho.starter.transform.factory.TiDictTransFactory;

import java.io.IOException;

/**
 * 字典转换序列化器
 *
 * @author zhajianjun
 * @date 2025-03-24 22:15
 */
@Setter
public class TiDictTransSerializer extends StdSerializer<Object> implements ContextualSerializer {

    private TiDictTrans tiDictTrans;
    private TiCacheTemplate tiCacheTemplate;

    public TiDictTransSerializer() {
        super(Object.class);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // 检查beanProperty是否为null，如果为null则直接返回null值的序列化器
        if (beanProperty != null) {
            // 获取属性的原始类型
            Class<?> rawClass = beanProperty.getType().getRawClass();
            // 检查属性类型是否为基本类型或String类型
            if (ClassUtil.isBasicType(rawClass) || rawClass == String.class) {
                // 尝试从属性上获取TiDictTrans注解
                TiDictTrans annotation = beanProperty.getAnnotation(TiDictTrans.class);
                if (annotation == null) {
                    // 如果属性上没有该注解，则尝试从上下文中获取
                    annotation = beanProperty.getContextAnnotation(TiDictTrans.class);
                }
                // 如果找到TiDictTrans注解，则创建并配置TiDictTransSerializer
                if (annotation != null) {
                    return TiDictTransFactory.createSerializer(annotation);
                }
            }
            // 如果没有找到TiDictTrans注解，则返回默认的序列化器
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (tiDictTrans == null || value == null) {
            gen.writeObject(value);
            return;
        }
        String cacheName = tiDictTrans.cacheName();
        String dictType = tiDictTrans.dictType();
        Object render = tiCacheTemplate.get(cacheName, dictType);
        gen.writeObject(render);
    }

}
