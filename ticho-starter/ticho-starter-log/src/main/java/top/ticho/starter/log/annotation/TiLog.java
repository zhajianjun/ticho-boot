package top.ticho.starter.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhajianjun
 * @date 2024-12-25 22:51
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TiLog {

    /**
     * 接口名
     *
     * @return {@link String }
     */
    String value();

}
