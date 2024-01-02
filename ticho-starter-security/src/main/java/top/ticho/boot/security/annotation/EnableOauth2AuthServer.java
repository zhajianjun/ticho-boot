package top.ticho.boot.security.annotation;

import top.ticho.boot.security.controller.OauthController;
import top.ticho.boot.security.handle.BaseLoginUserHandle;
import top.ticho.boot.security.handle.jwt.JwtEncode;
import top.ticho.boot.security.handle.load.BaseLoadUserService;
import top.ticho.boot.security.prop.BaseOauthProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// @formatter:off

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
public @interface EnableOauth2AuthServer {

}
