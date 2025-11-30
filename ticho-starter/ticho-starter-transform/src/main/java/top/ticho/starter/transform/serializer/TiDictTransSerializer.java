package top.ticho.starter.transform.serializer;

import lombok.Setter;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import top.ticho.starter.transform.annotation.TiDictTrans;
import top.ticho.starter.transform.factory.TiDictTransFactory;
import top.ticho.tool.core.TiClassUtil;

import java.util.Map;

/**
 * 字典转换序列化器
 *
 * @author zhajianjun
 * @date 2025-03-24 22:15
 */
@Setter
public class TiDictTransSerializer extends StdSerializer<Object> {
    private Map<String, String> dictMap;

    public TiDictTransSerializer() {
        super(Object.class);
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext context, BeanProperty beanProperty) {
        // 检查beanProperty是否为null，如果为null则直接返回null值的序列化器
        if (beanProperty != null) {
            // 获取属性的原始类型
            Class<?> rawClass = beanProperty.getType().getRawClass();
            // 检查属性类型是否为基本类型或String类型
            if (TiClassUtil.isSimpleValueType(rawClass) || rawClass == String.class) {
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
            return context.findValueSerializer(beanProperty.getType());
        }
        return context.findNullValueSerializer(null);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext context) throws JacksonException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        // 添加类型检查
        if (!(value instanceof String)) {
            gen.writePOJO(value);
            return;
        }
        String valueStr = value.toString();
        gen.writePOJO(dictMap.getOrDefault(valueStr, valueStr));
    }

}
