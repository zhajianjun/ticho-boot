package top.ticho.boot.security.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略token校验
 *
 * <p>
*     和PreAuthorize有实际意义上的冲突，不要联合使用，token都不校验了，权限就无法验证了
 *   @see org.springframework.security.access.prepost.PreAuthorize
 * </p>
 *
 * @author zhajianjun
 * @date 2022-09-26 14:11:34
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreJwtCheck {

    /** 忽略检查类型 */
    @AliasFor("type")
    IgnoreType value() default IgnoreType.ALL;

    @AliasFor("value")
    IgnoreType type() default IgnoreType.ALL;

}
