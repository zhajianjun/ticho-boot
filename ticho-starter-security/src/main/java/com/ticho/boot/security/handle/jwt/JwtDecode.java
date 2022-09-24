package com.ticho.boot.security.handle.jwt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.exception.BizException;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-24 13:45:19
 */
@Slf4j
public class JwtDecode {

    public final JwtConverter jwtConverter;

    public JwtDecode(JwtConverter jwtConverter) {
        Assert.isNotNull(jwtConverter, BizErrCode.FAIL, "signer is null");
        this.jwtConverter = jwtConverter;
    }

    public Map<String, Object> decode(String token) {
        String claims = JwtHelper.decode(token).getClaims();
        return JsonUtil.toMap(claims, String.class, Object.class);
    }

    public Map<String, Object> decodeAndVerify(String token) {
        SignatureVerifier verifier = jwtConverter.getVerifier();
        Assert.isNotNull(verifier, BizErrCode.FAIL, "verifier is null");
        String claims;
        try {
            claims = JwtHelper.decodeAndVerify(token, verifier).getClaims();
        } catch (Exception e) {
            log.error("token验证失败, {}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "token验证失败");
        }
        Assert.isNotNull(claims, BizErrCode.FAIL, "token验证失败");
        Map<String, Object> map = JsonUtil.toMap(claims, String.class, Object.class);
        boolean isExpired = false;
        if (CollUtil.isEmpty(map) || !map.containsKey(SecurityConst.EXP)) {
            isExpired = true;
        }
        if (!isExpired) {
            String numberStr = Optional.ofNullable(map.get(SecurityConst.EXP)).map(Object::toString).orElse(null);
            BigDecimal exp = NumberUtil.toBigDecimal(numberStr);
            isExpired = exp.longValue() < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }
        Assert.isTrue(!isExpired, BizErrCode.FAIL, "token过期");
        return map;
    }

}
