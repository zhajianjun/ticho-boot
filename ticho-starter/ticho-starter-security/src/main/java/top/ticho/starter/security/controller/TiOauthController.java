package top.ticho.starter.security.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ticho.starter.security.dto.TiLoginRequest;
import top.ticho.starter.security.dto.TiToken;
import top.ticho.starter.security.service.TiLoginService;
import top.ticho.starter.view.core.TiResult;

import java.security.Principal;

/**
 * 权限用户登录
 *
 * @author zhajianjun
 * @date 2022-09-22 15:36
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("oauth")
public class TiOauthController {
    private final TiLoginService tiLoginService;

    /**
     * 登录
     *
     * @param loginRequest 登录请求
     * @return {@link TiResult }<{@link TiToken }>
     */
    @PostMapping("token")
    public TiResult<TiToken> token(TiLoginRequest loginRequest) {
        return TiResult.ok(tiLoginService.token(loginRequest));
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return {@link TiResult }<{@link TiToken }>
     */
    @PostMapping("refreshToken")
    public TiResult<TiToken> refreshToken(String refreshToken) {
        return TiResult.ok(tiLoginService.refreshToken(refreshToken));
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
        return TiResult.ok(tiLoginService.publicKey());
    }


}
