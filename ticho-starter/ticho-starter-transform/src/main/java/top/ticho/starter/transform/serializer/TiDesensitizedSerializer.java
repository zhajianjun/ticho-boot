package top.ticho.starter.transform.serializer;

import lombok.Setter;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import top.ticho.starter.transform.annotation.TiDesensitized;
import top.ticho.starter.transform.enums.TiDesensitizedType;
import top.ticho.tool.core.TiClassUtil;
import top.ticho.tool.core.TiDesensitizedUtil;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.constant.TiStrConst;

/**
 * 脱敏序列化器
 *
 * @author zhajianjun
 * @date 2025-03-21 21:44
 */
@Setter
public class TiDesensitizedSerializer extends StdSerializer<Object> {

    private TiDesensitized tiDesensitized;

    public TiDesensitizedSerializer() {
        super(Object.class);
    }

    /**
     * 根据上下文创建适当的JsonSerializer实例。
     * 该方法用于在序列化过程中根据Bean属性的类型和注解动态选择或创建序列化器。
     *
     * @param context      提供序列化器的上下文环境，用于查找或创建序列化器。
     * @param beanProperty 当前正在处理的Bean属性，包含属性的类型和注解信息。
     * @return 返回一个适合当前属性的JsonSerializer实例。如果属性为null，则返回null值的序列化器。
     */
    @Override
    public ValueSerializer<Object> createContextual(SerializationContext context, BeanProperty beanProperty) {
        // 检查beanProperty是否为null，如果为null则直接返回null值的序列化器
        if (beanProperty != null) {
            // 获取属性的原始类型
            Class<?> rawClass = beanProperty.getType().getRawClass();
            // 检查属性类型是否为基本类型或String类型
            if (TiClassUtil.isSimpleValueType(rawClass) || rawClass == String.class) {
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
            return context.findValueSerializer(beanProperty.getType());
        }
        return context.findNullValueSerializer(null);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext context) throws JacksonException {
        if (tiDesensitized == null || value == null) {
            gen.writePOJO(value);
            return;
        }
        String render = desensitized(value.toString(), tiDesensitized);
        gen.writePOJO(render);
    }

    public static String desensitized(String str, TiDesensitized tiDesensitized) {
        if (TiStrUtil.isBlank(str)) {
            return TiStrConst.EMPTY;
        }
        TiDesensitizedType tiDesensitizedType = tiDesensitized.type();
        return switch (tiDesensitizedType) {
            case USER_ID -> String.valueOf(TiDesensitizedUtil.userId());
            case CHINESE_NAME -> TiDesensitizedUtil.chineseName(str);
            case ID_CARD -> TiDesensitizedUtil.idCardNum(str, 1, 2);
            case FIXED_PHONE -> TiDesensitizedUtil.fixedPhone(str);
            case MOBILE_PHONE -> TiDesensitizedUtil.mobilePhone(str);
            case ADDRESS -> TiDesensitizedUtil.address(str, 8);
            case EMAIL -> TiDesensitizedUtil.email(str);
            case PASSWORD -> TiDesensitizedUtil.password(str);
            case CAR_LICENSE -> TiDesensitizedUtil.carLicense(str);
            case BANK_CARD -> TiDesensitizedUtil.bankCard(str);
            case IPV4 -> TiDesensitizedUtil.ipv4(str);
            case IPV6 -> TiDesensitizedUtil.ipv6(str);
            case FIRST_MASK -> TiDesensitizedUtil.firstMask(str);
            default -> TiStrUtil.hide(str, tiDesensitized.start(), tiDesensitized.end());
        };
    }

}
