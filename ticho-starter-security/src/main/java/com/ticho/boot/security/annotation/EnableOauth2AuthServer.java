package com.ticho.boot.security.annotation;

import com.ticho.boot.security.controller.OauthController;
import com.ticho.boot.security.handle.TichoLoginUserHandle;
import com.ticho.boot.security.handle.jwt.JwtEncode;
import com.ticho.boot.security.handle.load.TichoPhoneLoadUserService;
import com.ticho.boot.security.handle.load.TichoUsernameLoadUserService;
import com.ticho.boot.security.handle.login.TichoUsernameLoginUserStragety;
import com.ticho.boot.security.prop.TichoOauthProperty;
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
    TichoOauthProperty.class,
    JwtEncode.class,
    TichoUsernameLoadUserService.class,
    TichoPhoneLoadUserService.class,
    TichoLoginUserHandle.class,
    TichoUsernameLoginUserStragety.class,
    TichoUsernameLoginUserStragety.class,
    TichoUsernameLoginUserStragety.class,
    OauthController.class,
})
@ConditionalOnWebApplication
public @interface EnableOauth2AuthServer {

}
