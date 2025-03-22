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

    @Override
    public JsonSerializer<Object> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            Class<?> rawClass = beanProperty.getType().getRawClass();
            if (ClassUtil.isBasicType(rawClass) || rawClass == String.class) {
                TiDesensitized annotation = beanProperty.getAnnotation(TiDesensitized.class);
                if (annotation == null) {
                    annotation = beanProperty.getContextAnnotation(TiDesensitized.class);
                }
                if (annotation != null) {
                    TiDesensitizedSerializer tiDesensitizedSerializer = new TiDesensitizedSerializer();
                    tiDesensitizedSerializer.setTiDesensitized(annotation);
                    return tiDesensitizedSerializer;
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (tiDesensitized == null || value == null) {
            gen.writeObject(value);
            return;
        }
        try {
            String render = desensitized(value.toString(), tiDesensitized);
            gen.writeObject(render);
        } catch (Exception e) {
            gen.writeObject(value);
        }
    }

    public static String desensitized(CharSequence str, TiDesensitized tiDesensitized) {
        if (StrUtil.isBlank(str)) {
            return StrUtil.EMPTY;
        }
        TiDesensitizedType tiDesensitizedType = tiDesensitized.type();
        String newStr = String.valueOf(str);
        switch (tiDesensitizedType) {
            case USER_ID:
                newStr = String.valueOf(DesensitizedUtil.userId());
                break;
            case CHINESE_NAME:
                newStr = DesensitizedUtil.chineseName(String.valueOf(str));
                break;
            case ID_CARD:
                newStr = DesensitizedUtil.idCardNum(String.valueOf(str), 1, 2);
                break;
            case FIXED_PHONE:
                newStr = DesensitizedUtil.fixedPhone(String.valueOf(str));
                break;
            case MOBILE_PHONE:
                newStr = DesensitizedUtil.mobilePhone(String.valueOf(str));
                break;
            case ADDRESS:
                newStr = DesensitizedUtil.address(String.valueOf(str), 8);
                break;
            case EMAIL:
                newStr = DesensitizedUtil.email(String.valueOf(str));
                break;
            case PASSWORD:
                newStr = DesensitizedUtil.password(String.valueOf(str));
                break;
            case CAR_LICENSE:
                newStr = DesensitizedUtil.carLicense(String.valueOf(str));
                break;
            case BANK_CARD:
                newStr = DesensitizedUtil.bankCard(String.valueOf(str));
                break;
            case IPV4:
                newStr = DesensitizedUtil.ipv4(String.valueOf(str));
                break;
            case IPV6:
                newStr = DesensitizedUtil.ipv6(String.valueOf(str));
                break;
            case FIRST_MASK:
                newStr = DesensitizedUtil.firstMask(String.valueOf(str));
                break;
            default:
                newStr = StrUtil.hide(String.valueOf(str), tiDesensitized.start(), tiDesensitized.end());
                break;
        }
        return newStr;
    }


}
