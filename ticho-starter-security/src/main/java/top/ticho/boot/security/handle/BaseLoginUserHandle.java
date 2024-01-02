package top.ticho.boot.security.handle;

import top.ticho.boot.security.constant.BaseSecurityConst;
import top.ticho.boot.security.dto.LoginRequest;
import top.ticho.boot.security.dto.Oauth2AccessToken;
import top.ticho.boot.security.handle.jwt.JwtDecode;
import top.ticho.boot.security.handle.jwt.JwtSigner;
import top.ticho.boot.security.handle.load.LoadUserService;
import top.ticho.boot.view.core.BaseSecurityUser;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.enums.HttpErrCode;
import top.ticho.boot.view.exception.BizException;
import top.ticho.boot.view.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 默认登录处理接口实现
 *
 * @author zhajianjun
 * @date 2022-09-24 16:29:51
 */
@ConditionalOnMissingBean(LoginUserHandle.class)
@Component
public class BaseLoginUserHandle extends AbstractLoginUserHandle {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private LoadUserService loadUserService;

    @Autowired
    private JwtDecode jwtDecode;

    @Autowired
    private JwtSigner jwtSigner;

    public Oauth2AccessToken token(LoginRequest loginRequest) {
        String account = loginRequest.getUsername();
        String credentials = loginRequest.getPassword();
        BaseSecurityUser baseSecurityUser = checkPassword(account, credentials);
        return getOauth2TokenAndSetAuthentication(baseSecurityUser);
        // @formatter:on
    }

    public BaseSecurityUser checkPassword(String account, String credentials) {
        // @formatter:off
        // 查询用户信息
        Assert.isNotNull(account, BizErrCode.PARAM_ERROR, "用户名不能为空");
        Assert.isNotNull(credentials, BizErrCode.PARAM_ERROR, "密码不能为空");
        BaseSecurityUser baseSecurityUser = loadUserService.load(account);
        Assert.isNotNull(baseSecurityUser, HttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        // 校验用户密码
        String passwordAes = baseSecurityUser.getPassword();
        Assert.isTrue(passwordEncoder.matches(credentials, passwordAes), HttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        return baseSecurityUser;
        // @formatter:on
    }

    public Oauth2AccessToken refreshToken(String refreshToken) {
        Assert.isNotNull(refreshToken, BizErrCode.PARAM_ERROR, "参数不能为空");
        // @formatter:off
        Map<String, Object> decodeAndVerify = jwtDecode.decodeAndVerify(refreshToken);
        Object type = decodeAndVerify.getOrDefault(BaseSecurityConst.TYPE, "");
        Assert.isTrue(Objects.equals(type, BaseSecurityConst.REFRESH_TOKEN), BizErrCode.FAIL, "refreshToken不合法");
        String username = Optional.ofNullable(decodeAndVerify.get(BaseSecurityConst.USERNAME))
            .map(Object::toString)
            .orElseThrow(()-> new BizException(BizErrCode.FAIL, "用户名不存在"));
        BaseSecurityUser baseSecurityUser = loadUserService.load(username);
        return getOauth2TokenAndSetAuthentication(baseSecurityUser);
        // @formatter:on
    }

    @Override
    public String publicKey() {
        return jwtSigner.getVerifierKey();
        // @formatter:on
    }
}
