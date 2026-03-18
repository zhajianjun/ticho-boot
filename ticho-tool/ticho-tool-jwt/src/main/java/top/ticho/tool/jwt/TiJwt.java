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
 * JWT（JSON Web Token）记录类，封装了 Token 解析后的核心信息
 * <p>包含原始 Token 字符串、解析后的 TokenizedJwt 对象、请求头信息和声明信息</p>
 * <p>提供 Token 有效性验证、签名验证和过期检查等功能</p>
 *
 * @author zhajianjun
 * @date 2026-01-01 14:28
 */
public record TiJwt(String token, TokenizedJwt tokenized, Header header, Claims claims) {

    /**
     * 验证 Token 是否有效（使用 SM2 公钥验签）
     *
     * @param publicKeyBytes SM2 公钥字节数组
     * @return Token 有效返回 true，否则返回 false
     */
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

    /**
     * 验证 Token 的签名（使用 SM2 公钥验签），验签失败时抛出异常
     *
     * @param publicKeyBytes SM2 公钥字节数组
     * @throws TiUtilException 当请求头无效、算法为空或验签失败时抛出
     */
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

    /**
     * 验证 Token 是否过期（不允许时钟偏移）
     *
     * @throws TiUtilException 当 Token 已过期时抛出
     */
    public void verifyExpired() {
        verifyExpired(0);
    }

    /**
     * 验证 Token 是否过期（可设置允许的时钟偏移）
     *
     * @param allowedClockSkewMillis 允许的时钟偏移毫秒数，用于处理服务器间时间不同步的情况
     * @throws TiUtilException 当 Token 已过期时抛出
     */
    public void verifyExpired(long allowedClockSkewMillis) {
        boolean expired = isExpired(allowedClockSkewMillis);
        if (expired) {
            throw new TiUtilException("Token 已过期");
        }
    }

    /**
     * 检查 Token 是否过期（不允许时钟偏移）
     *
     * @return Token 已过期返回 true，否则返回 false
     */
    public boolean isExpired() {
        return isExpired(0);
    }

    /**
     * 检查 Token 是否过期（可设置允许的时钟偏移）
     *
     * @param allowedClockSkewMillis 允许的时钟偏移毫秒数，用于处理服务器间时间不同步的情况
     * @return Token 已过期返回 true，否则返回 false
     */
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
