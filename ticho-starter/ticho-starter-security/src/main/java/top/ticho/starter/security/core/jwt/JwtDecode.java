package top.ticho.starter.security.core.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.tool.core.TiAssert;
import top.ticho.tool.core.TiMapUtil;
import top.ticho.tool.core.TiNumberUtil;
import top.ticho.tool.core.enums.TiBizErrorCode;
import top.ticho.tool.core.exception.TiBizException;
import top.ticho.tool.json.util.TiJsonUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * jwt解密
 *
 * @author zhajianjun
 * @date 2022-09-24 13:45
 */
@Slf4j
public record JwtDecode(JwtSigner jwtSigner) {

    public JwtDecode {
        TiAssert.isNotNull(jwtSigner, TiBizErrorCode.FAIL, "signer is null");
    }

    public Map<String, Object> decode(String token) {
        String claims = JwtHelper.decode(token).getClaims();
        return TiJsonUtil.toMap(claims, String.class, Object.class);
    }

    public Map<String, Object> decodeAndVerify(String token) {
        SignatureVerifier verifier = jwtSigner.getVerifier();
        TiAssert.isNotNull(verifier, TiBizErrorCode.FAIL, "verifier is null");
        String claims;
        try {
            claims = JwtHelper.decodeAndVerify(token, verifier).getClaims();
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "TOKEN 失效", e);
        }
        TiAssert.isNotNull(claims, TiBizErrorCode.FAIL, "TOKEN 失效");
        Map<String, Object> map = TiJsonUtil.toMap(claims, String.class, Object.class);
        boolean isExpired = isExpired(map);
        TiAssert.isTrue(!isExpired, TiBizErrorCode.FAIL, "token过期");
        return map;
    }

    public boolean isExpired(Map<String, Object> map) {
        boolean isExpired = TiMapUtil.isEmpty(map) || !map.containsKey(TiSecurityConst.EXP);
        if (!isExpired) {
            String numberStr = Optional.ofNullable(map.get(TiSecurityConst.EXP)).map(Object::toString).orElse(null);
            BigDecimal exp = TiNumberUtil.toBigDecimal(numberStr);
            isExpired = exp.longValue() < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }
        return isExpired;
    }

}
