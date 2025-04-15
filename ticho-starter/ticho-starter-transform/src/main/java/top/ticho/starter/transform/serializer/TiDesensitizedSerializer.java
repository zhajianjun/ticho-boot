package top.ticho.starter.transform.serializer;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Setter;
import top.ticho.starter.transform.annotation.TiDesensitized;
import top.ticho.starter.transform.enums.TiDesensitizedType;

import java.io.IOException;

/**
 * 脱敏序列化器
 *
 * @author zhajianjun
 * @date 2025-03-21 21:44
 */
@Setter
public class TiDesensitizedSerializer extends StdSerializer<Object> implements ContextualSerializer {

    private TiDesensitized tiDesensitized;

    public TiDesensitizedSerializer() {
        super(Object.class);
    }

    /**
     * 根据上下文创建适当的JsonSerializer实例。
     * 该方法用于在序列化过程中根据Bean属性的类型和注解动态选择或创建序列化器。
     *
     * @param serializerProvider 提供序列化器的上下文环境，用于查找或创建序列化器。
     * @param beanProperty       当前正在处理的Bean属性，包含属性的类型和注解信息。
     * @return 返回一个适合当前属性的JsonSerializer实例。如果属性为null，则返回null值的序列化器。
     * @throws JsonMappingException 如果在创建或查找序列化器过程中发生错误。
     */
    @Override
    public JsonSerializer<Object> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // 检查beanProperty是否为null，如果为null则直接返回null值的序列化器
        if (beanProperty != null) {
            // 获取属性的原始类型
            Class<?> rawClass = beanProperty.getType().getRawClass();
            // 检查属性类型是否为基本类型或String类型
            if (ClassUtil.isBasicType(rawClass) || rawClass == String.class) {
                // 尝试从属性上获取TiDesensitized注解
                TiDesensitized annotation = beanProperty.getAnnotation(TiDesensitized.class);
                if (annotation == null) {
                    // 如果属性上没有该注解，则尝试从上下文中获取
                    annotation = beanProperty.getContextAnnotation(TiDesensitized.class);
                }
                // 如果找到TiDesensitized注解，则创建并配置TiDesensitizedSerializer
                if (annotation != null) {
                    TiDesensitizedSerializer tiDesensitizedSerializer = new TiDesensitizedSerializer();
                    tiDesensitizedSerializer.setTiDesensitized(annotation);
                    return tiDesensitizedSerializer;
                }
            }
            // 如果没有找到TiDesensitized注解，则返回默认的序列化器
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (tiDesensitized == null || value == null) {
            gen.writeObject(value);
            return;
        }
        String render = desensitized(value.toString(), tiDesensitized);
        gen.writeObject(render);
    }

    public static String desensitized(CharSequence str, TiDesensitized tiDesensitized) {
        if (StrUtil.isBlank(str)) {
            return StrUtil.EMPTY;
        }
        TiDesensitizedType tiDesensitizedType = tiDesensitized.type();
        return switch (tiDesensitizedType) {
            case USER_ID -> String.valueOf(DesensitizedUtil.userId());
            case CHINESE_NAME -> DesensitizedUtil.chineseName(String.valueOf(str));
            case ID_CARD -> DesensitizedUtil.idCardNum(String.valueOf(str), 1, 2);
            case FIXED_PHONE -> DesensitizedUtil.fixedPhone(String.valueOf(str));
            case MOBILE_PHONE -> DesensitizedUtil.mobilePhone(String.valueOf(str));
            case ADDRESS -> DesensitizedUtil.address(String.valueOf(str), 8);
            case EMAIL -> DesensitizedUtil.email(String.valueOf(str));
            case PASSWORD -> DesensitizedUtil.password(String.valueOf(str));
            case CAR_LICENSE -> DesensitizedUtil.carLicense(String.valueOf(str));
            case BANK_CARD -> DesensitizedUtil.bankCard(String.valueOf(str));
            case IPV4 -> DesensitizedUtil.ipv4(String.valueOf(str));
            case IPV6 -> DesensitizedUtil.ipv6(String.valueOf(str));
            case FIRST_MASK -> DesensitizedUtil.firstMask(String.valueOf(str));
            default -> StrUtil.hide(String.valueOf(str), tiDesensitized.start(), tiDesensitized.end());
        };
    }

}
