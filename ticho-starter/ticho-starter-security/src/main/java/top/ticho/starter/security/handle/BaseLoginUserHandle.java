package top.ticho.starter.security.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.ticho.starter.security.constant.BaseSecurityConst;
import top.ticho.starter.security.dto.LoginRequest;
import top.ticho.starter.security.dto.Oauth2AccessToken;
import top.ticho.starter.security.handle.jwt.JwtDecode;
import top.ticho.starter.security.handle.jwt.JwtSigner;
import top.ticho.starter.security.handle.load.LoadUserService;
import top.ticho.boot.view.core.TiSecurityUser;
import top.ticho.boot.view.enums.TiBizErrCode;
import top.ticho.boot.view.enums.TiHttpErrCode;
import top.ticho.boot.view.exception.TiBizException;
import top.ticho.boot.view.util.TiAssert;

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
        TiSecurityUser tiSecurityUser = checkPassword(account, credentials);
        return getOauth2TokenAndSetAuthentication(tiSecurityUser);
    }

    public TiSecurityUser checkPassword(String account, String credentials) {
        // 查询用户信息
        TiAssert.isNotNull(account, TiBizErrCode.PARAM_ERROR, "用户名不能为空");
        TiAssert.isNotNull(credentials, TiBizErrCode.PARAM_ERROR, "密码不能为空");
        TiSecurityUser tiSecurityUser = loadUserService.load(account);
        TiAssert.isNotNull(tiSecurityUser, TiHttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        // 校验用户密码
        String passwordAes = tiSecurityUser.getPassword();
        TiAssert.isTrue(passwordEncoder.matches(credentials, passwordAes), TiHttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        return tiSecurityUser;
    }

    public Oauth2AccessToken refreshToken(String refreshToken) {
        TiAssert.isNotNull(refreshToken, TiBizErrCode.PARAM_ERROR, "参数不能为空");
        Map<String, Object> decodeAndVerify = jwtDecode.decodeAndVerify(refreshToken);
        Object type = decodeAndVerify.getOrDefault(BaseSecurityConst.TYPE, "");
        TiAssert.isTrue(Objects.equals(type, BaseSecurityConst.REFRESH_TOKEN), TiBizErrCode.FAIL, "refreshToken不合法");
        String username = Optional.ofNullable(decodeAndVerify.get(BaseSecurityConst.USERNAME))
            .map(Object::toString)
            .orElseThrow(() -> new TiBizException(TiBizErrCode.FAIL, "用户名不存在"));
        TiSecurityUser tiSecurityUser = loadUserService.load(username);
        return getOauth2TokenAndSetAuthentication(tiSecurityUser);
    }

    @Override
    public String publicKey() {
        return jwtSigner.getVerifierKey();
    }
}
