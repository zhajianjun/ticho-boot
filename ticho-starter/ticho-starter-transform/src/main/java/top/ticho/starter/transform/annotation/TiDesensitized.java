package top.ticho.starter.transform.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import top.ticho.starter.transform.enums.TiDesensitizedType;
import top.ticho.starter.transform.serializer.TiDesensitizedSerializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 *
 * @author zhajianjun
 * @date 2025-03-22 16:28
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = TiDesensitizedSerializer.class)
@Documented
public @interface TiDesensitized {

    /**
     * 类型
     *
     * @return {@link TiDesensitizedType }
     */
    TiDesensitizedType type() default TiDesensitizedType.DEFAULT;

    /**
     * 开始位置（包含）
     *
     * @return int
     */
    int start() default 0;

    /**
     * 结束位置（不包含）
     *
     * @return int
     */
    int end() default Integer.MAX_VALUE;

}
