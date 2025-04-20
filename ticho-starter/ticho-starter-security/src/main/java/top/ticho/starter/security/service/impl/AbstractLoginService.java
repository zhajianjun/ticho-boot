package top.ticho.starter.security.service.impl;

import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.core.jwt.JwtDecode;
import top.ticho.starter.security.core.jwt.JwtEncode;
import top.ticho.starter.security.core.jwt.JwtExtra;
import top.ticho.starter.security.core.jwt.JwtSigner;
import top.ticho.starter.security.dto.LoginRequest;
import top.ticho.starter.security.dto.TiToken;
import top.ticho.starter.security.service.TiLoginService;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.enums.TiHttpErrCode;
import top.ticho.starter.view.exception.TiBizException;
import top.ticho.starter.view.util.TiAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 登录处理抽象类
 * <p> 抽取公共方法 </p>
 *
 * @author zhajianjun
 * @date 2022-09-24 16:29:51
 */
public abstract class AbstractLoginService implements TiLoginService {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtSigner jwtSigner;
    @Resource
    private JwtEncode jwtEncode;
    @Resource
    private JwtDecode jwtDecode;
    @Resource
    private Map<String, JwtExtra> jwtExtInfoMap;


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
        TiSecurityUser tiSecurityUser = load(account);
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
        TiSecurityUser tiSecurityUser = load(username);
        return toToken(tiSecurityUser);
    }

    @Override
    public String publicKey() {
        return jwtSigner.getVerifierKey();
    }

    /**
     * 根据权限用户获取token返回信息
     *
     * @param tiSecurityUser 权限用户
     * @return token返回信息
     */
    protected TiToken toToken(TiSecurityUser tiSecurityUser) {
        List<String> authoritieStrs = Optional.ofNullable(tiSecurityUser.getRoles()).orElseGet(ArrayList::new);
        tiSecurityUser.setPassword("N/A");
        List<SimpleGrantedAuthority> authorities = authoritieStrs.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tiSecurityUser, tiSecurityUser.getPassword(), authorities);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest httpServletRequest = requestAttributes.getRequest();
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TiToken oAuth2AccessToken = new TiToken();
        Map<String, Object> map = getAllJwtExtInfo();
        // jwt扩展接口的所有数据放入token中
        oAuth2AccessToken.setExtInfo(map);
        jwtEncode.encode(oAuth2AccessToken, tiSecurityUser);
        return oAuth2AccessToken;
    }

    /**
     * 获取jwt扩展接口的所有数据
     */
    private Map<String, Object> getAllJwtExtInfo() {
        Map<String, Object> map = new HashMap<>(8);
        for (JwtExtra value : jwtExtInfoMap.values()) {
            Map<String, Object> ext = value.getExtra();
            if (CollUtil.isEmpty(ext)) {
                continue;
            }
            map.putAll(ext);
        }
        return map;
    }

}
