package top.ticho.boot.security.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import top.ticho.boot.security.constant.BaseOAuth2Const;
import top.ticho.boot.security.dto.BaseLoginRequest;
import top.ticho.boot.security.dto.Oauth2AccessToken;
import top.ticho.boot.security.handle.LoginUserHandle;
import top.ticho.boot.view.core.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 权限用户登录
 *
 * @author zhajianjun
 * @date 2022-09-22 15:36
 */
@ConditionalOnMissingBean(name = BaseOAuth2Const.OAUTH2_CONTROLLER)
@RestController(BaseOAuth2Const.OAUTH2_CONTROLLER)
@RequestMapping("oauth")
@ApiSort(Ordered.HIGHEST_PRECEDENCE + 200)
@Api(tags = "权限用户登录")
public class OauthController {

    @Autowired
    private LoginUserHandle loginUserHandle;

    @ApiOperation("登录")
    @ApiOperationSupport(order = 10)
    @PostMapping("token")
    public Result<Oauth2AccessToken> token(BaseLoginRequest loginRequest) {
        return Result.ok(loginUserHandle.token(loginRequest));
    }

    @ApiOperation("刷新token")
    @ApiOperationSupport(order = 20)
    @PostMapping("refreshToken")
    public Result<Oauth2AccessToken> refreshToken(String refreshToken) {
        return Result.ok(loginUserHandle.refreshToken(refreshToken));
    }

    @ApiOperation(value = "token信息查询")
    @ApiOperationSupport(order = 30)
    @GetMapping
    public Result<Principal> principal() {
        return Result.ok(SecurityContextHolder.getContext().getAuthentication());
    }

    @ApiOperation("获取公钥")
    @ApiOperationSupport(order = 40)
    @GetMapping("publicKey")
    public Result<String> publicKey() {
        return Result.ok(loginUserHandle.publicKey());
    }


}
