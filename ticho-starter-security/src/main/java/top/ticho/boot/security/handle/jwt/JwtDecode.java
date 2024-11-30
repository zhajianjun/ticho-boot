package top.ticho.boot.security.handle.jwt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import top.ticho.boot.security.constant.BaseSecurityConst;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.exception.BizException;
import top.ticho.boot.view.util.Assert;
import top.ticho.tool.json.util.JsonUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * jwt解密
 *
 * @author zhajianjun
 * @date 2022-09-24 13:45:19
 */
@Slf4j
public class JwtDecode {

    public final JwtSigner jwtSigner;

    public JwtDecode(JwtSigner jwtSigner) {
        Assert.isNotNull(jwtSigner, BizErrCode.FAIL, "signer is null");
        this.jwtSigner = jwtSigner;
    }

    public Map<String, Object> decode(String token) {
        String claims = JwtHelper.decode(token).getClaims();
        return JsonUtil.toMap(claims, String.class, Object.class);
    }

    public Map<String, Object> decodeAndVerify(String token) {
        SignatureVerifier verifier = jwtSigner.getVerifier();
        Assert.isNotNull(verifier, BizErrCode.FAIL, "verifier is null");
        String claims;
        try {
            claims = JwtHelper.decodeAndVerify(token, verifier).getClaims();
        } catch (Exception e) {
            log.error("TOKEN 失效, {}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "TOKEN 失效");
        }
        Assert.isNotNull(claims, BizErrCode.FAIL, "TOKEN 失效");
        Map<String, Object> map = JsonUtil.toMap(claims, String.class, Object.class);
        boolean isExpired = isExpired(map);
        Assert.isTrue(!isExpired, BizErrCode.FAIL, "token过期");
        return map;
    }

    public boolean isExpired(Map<String, Object> map) {
        boolean isExpired = CollUtil.isEmpty(map) || !map.containsKey(BaseSecurityConst.EXP);
        if (!isExpired) {
            String numberStr = Optional.ofNullable(map.get(BaseSecurityConst.EXP)).map(Object::toString).orElse(null);
            BigDecimal exp = NumberUtil.toBigDecimal(numberStr);
            isExpired = exp.longValue() < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }
        return isExpired;
    }

}
