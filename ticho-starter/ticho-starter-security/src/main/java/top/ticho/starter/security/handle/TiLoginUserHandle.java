package top.ticho.starter.security.handle;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.dto.LoginRequest;
import top.ticho.starter.security.dto.TiToken;
import top.ticho.starter.security.handle.jwt.JwtDecode;
import top.ticho.starter.security.handle.jwt.JwtSigner;
import top.ticho.starter.security.handle.load.LoadUserService;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.enums.TiHttpErrCode;
import top.ticho.starter.view.exception.TiBizException;
import top.ticho.starter.view.util.TiAssert;

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
public class TiLoginUserHandle extends AbstractLoginUserHandle {
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private LoadUserService loadUserService;
    @Resource
    private JwtDecode jwtDecode;
    @Resource
    private JwtSigner jwtSigner;

    public TiToken token(LoginRequest loginRequest) {
        String account = loginRequest.getUsername();
        String credentials = loginRequest.getPassword();
        TiSecurityUser tiSecurityUser = checkPassword(account, credentials);
        return toToken(tiSecurityUser);
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

    public TiToken refreshToken(String refreshToken) {
        TiAssert.isNotNull(refreshToken, TiBizErrCode.PARAM_ERROR, "参数不能为空");
        Map<String, Object> decodeAndVerify = jwtDecode.decodeAndVerify(refreshToken);
        Object type = decodeAndVerify.getOrDefault(TiSecurityConst.TYPE, "");
        TiAssert.isTrue(Objects.equals(type, TiSecurityConst.REFRESH_TOKEN), TiBizErrCode.FAIL, "refreshToken不合法");
        String username = Optional.ofNullable(decodeAndVerify.get(TiSecurityConst.USERNAME))
            .map(Object::toString)
            .orElseThrow(() -> new TiBizException(TiBizErrCode.FAIL, "用户名不存在"));
        TiSecurityUser tiSecurityUser = loadUserService.load(username);
        return toToken(tiSecurityUser);
    }

    @Override
    public String publicKey() {
        return jwtSigner.getVerifierKey();
    }

}
