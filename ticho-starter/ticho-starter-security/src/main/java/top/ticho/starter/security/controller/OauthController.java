package top.ticho.starter.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ticho.starter.security.constant.BaseOAuth2Const;
import top.ticho.starter.security.dto.BaseLoginRequest;
import top.ticho.starter.security.dto.Oauth2AccessToken;
import top.ticho.starter.security.handle.LoginUserHandle;
import top.ticho.starter.view.core.TiResult;

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
public class OauthController {

    @Autowired
    private LoginUserHandle loginUserHandle;

    /**
     * 登录
     *
     * @param loginRequest 登录请求
     * @return {@link TiResult }<{@link Oauth2AccessToken }>
     */
    @PostMapping("token")
    public TiResult<Oauth2AccessToken> token(BaseLoginRequest loginRequest) {
        return TiResult.ok(loginUserHandle.token(loginRequest));
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return {@link TiResult }<{@link Oauth2AccessToken }>
     */
    @PostMapping("refreshToken")
    public TiResult<Oauth2AccessToken> refreshToken(String refreshToken) {
        return TiResult.ok(loginUserHandle.refreshToken(refreshToken));
    }

    /**
     * token信息查询
     *
     * @return {@link TiResult }<{@link Principal }>
     */
    @GetMapping
    public TiResult<Principal> principal() {
        return TiResult.ok(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 获取公钥
     *
     * @return {@link TiResult }<{@link String }>
     */
    @GetMapping("publicKey")
    public TiResult<String> publicKey() {
        return TiResult.ok(loginUserHandle.publicKey());
    }


}
