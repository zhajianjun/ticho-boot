package top.ticho.boot.dict.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import top.ticho.boot.dict.component.DictRendererSerializer;
import top.ticho.boot.dict.constont.DictRenderConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhajianjun
 * @date 2024-12-28 15:38
 */
@JacksonAnnotationsInside
@JsonSerialize(using = DictRendererSerializer.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DictRender {

    /**
     * 字典管理器
     *
     * @return {@link String }
     */
    String name() default DictRenderConst.DICT_NAME;

    /**
     * 类型
     *
     * @return {@link String }
     */
    String type();

    /**
     * 字典注入目标
     *
     * @return {@link String }
     */
    String target();

}
