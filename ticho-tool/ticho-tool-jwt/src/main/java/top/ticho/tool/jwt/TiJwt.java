package top.ticho.tool.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.impl.TokenizedJwt;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Strings;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.exception.TiUtilException;
import top.ticho.tool.security.TiSm2Util;

import java.util.Date;

/**
 *
 *
 * @author zhajianjun
 * @date 2026-01-01 14:28
 */
public record TiJwt(String token, TokenizedJwt tokenized, Header header, Claims claims) {

    public boolean isValid(byte[] publicKeyBytes) {
        if (header == null) {
            return false;
        }
        String alg = Strings.clean(header.getAlgorithm());
        if (TiStrUtil.isEmpty(alg)) {
            return false;
        }
        CharSequence digest = tokenized.getDigest();
        byte[] dataBytes = (tokenized.getProtected().toString() + "." + tokenized.getPayload()).getBytes();
        byte[] signature = Decoders.BASE64URL.decode(digest);
        return TiSm2Util.verify(dataBytes, signature, publicKeyBytes);
    }

    public void verify(byte[] publicKeyBytes) {
        if (header == null) {
            throw new TiUtilException("无效的JWT请求头，请求头为空");
        }
        String alg = Strings.clean(header.getAlgorithm());
        if (TiStrUtil.isEmpty(alg)) {
            throw new TiUtilException("无效的JWT请求头，算法为空");
        }
        CharSequence digest = tokenized.getDigest();
        byte[] dataBytes = (tokenized.getProtected().toString() + "." + tokenized.getPayload()).getBytes();
        byte[] signature = Decoders.BASE64URL.decode(digest);
        boolean verify = TiSm2Util.verify(dataBytes, signature, publicKeyBytes);
        if (!verify) {
            throw new TiUtilException("验签失败");
        }
    }

    public void verifyExpired() {
        verifyExpired(0);
    }

    public void verifyExpired(long allowedClockSkewMillis) {
        boolean expired = isExpired(allowedClockSkewMillis);
        if (expired) {
            throw new TiUtilException("Token已过期");
        }
    }

    public boolean isExpired() {
        return isExpired(0);
    }

    public boolean isExpired(long allowedClockSkewMillis) {
        boolean allowSkew = allowedClockSkewMillis > 0;
        Date now = new Date();
        long nowTime = now.getTime();
        Date exp = claims.getExpiration();
        if (exp == null) {
            return false;
        }
        long maxTime = nowTime - allowedClockSkewMillis;
        Date max = allowSkew ? new Date(maxTime) : now;
        return max.after(exp);
    }

}
