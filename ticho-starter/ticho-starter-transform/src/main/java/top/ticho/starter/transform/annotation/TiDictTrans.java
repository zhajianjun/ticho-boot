package top.ticho.starter.transform.annotation;

/**
 * @author zhajianjun
 * @date 2025-03-24 22:14
 */

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import top.ticho.starter.transform.serializer.TiDesensitizedSerializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典转换注解
 *
 * @author zhajianjun@date 2025-03-24 22:15
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = TiDesensitizedSerializer.class)
@Documented
public @interface TiDictTrans {

    String cacheName() default "default";
    
    String dictType();

    String dictKey();

}
