package top.ticho.boot.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包装视图注解
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    /**
     * 是否忽略，只对action对应的方法有效
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean ignore() default false;

}
