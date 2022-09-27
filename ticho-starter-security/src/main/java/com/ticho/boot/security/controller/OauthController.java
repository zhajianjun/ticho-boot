package com.ticho.boot.security.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.dto.LoginRequest;
import com.ticho.boot.security.dto.Oauth2AccessToken;
import com.ticho.boot.security.handle.LoginUserHandle;
import com.ticho.boot.web.annotation.View;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限用户登录控制器
 *
 * @author zhajianjun
 * @date 2022-09-22 15:36
 */
@ConditionalOnMissingBean(name = OAuth2Const.OAUTH2_CONTROLLER)
@RestController(OAuth2Const.OAUTH2_CONTROLLER)
@RequestMapping("oauth")
@ApiSort(Ordered.HIGHEST_PRECEDENCE + 200)
@Api(tags = "权限用户登录")
@View
public class OauthController {

    @Autowired
    private LoginUserHandle loginUserHandle;

    @View(ignore = true)
    @ApiOperation("登录")
    @ApiOperationSupport(order = 10)
    @PostMapping("token")
    public Oauth2AccessToken token(LoginRequest loginRequest) {
        return loginUserHandle.token(loginRequest);
    }

    @View(ignore = true)
    @ApiOperation("刷新token")
    @ApiOperationSupport(order = 20)
    @PostMapping("refreshToken")
    public Oauth2AccessToken refreshToken(String refreshToken) {
        return loginUserHandle.refreshToken(refreshToken);
    }

    @ApiOperation("获取公钥")
    @ApiOperationSupport(order = 30)
    @GetMapping("publicKey")
    public String getPublicKey() {
        return loginUserHandle.getPublicKey();
    }


}
