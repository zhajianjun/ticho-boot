package top.ticho.starter.security.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;
import top.ticho.starter.security.controller.OauthController;
import top.ticho.starter.security.handle.BaseLoginUserHandle;
import top.ticho.starter.security.handle.jwt.JwtEncode;
import top.ticho.starter.security.handle.load.BaseLoadUserService;
import top.ticho.starter.security.prop.BaseOauthProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * 开启Oauth权限服务
 *
 * @author zhajianjun
 * @date 2022-09-24 22:47:37
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({
    BaseOauthProperty.class,
    JwtEncode.class,
    BaseLoadUserService.class,
    BaseLoginUserHandle.class,
    OauthController.class,
})
@ConditionalOnWebApplication
public @interface EnableTiSecurity {

}
