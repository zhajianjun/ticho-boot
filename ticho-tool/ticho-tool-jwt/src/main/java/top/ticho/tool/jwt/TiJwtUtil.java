package top.ticho.tool.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import top.ticho.tool.core.TiBase64Util;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.exception.TiUtilException;
import top.ticho.tool.security.TiSm2Util;

import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author zhajianjun
 * @date 2025-12-27 14:28
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TiJwtUtil {
    public static final String TYP = "typ";
    public static final String JWT = "JWT";

    /**
     * 使用SM2私钥生成JWT Token（非对称加密）
     *
     * @param claims          token数据
     * @param privateKeyBytes 国密私钥
     * @param expirationTime  有效期 毫秒
     * @return {@link String }
     */
    public static String generateTokenWithSM2(Map<String, Object> claims, byte[] privateKeyBytes, long expirationTime) {
        if (claims == null || privateKeyBytes == null || expirationTime <= 0) {
            throw new TiUtilException("TOKEN生成异常，参数不合法");
        }
        // 设置过期时间
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);
        return Jwts.builder()
            .header()
            .add(TYP, JWT)
            .and()
            .claims(claims)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(TiSm2Util.loadPrivateKey(privateKeyBytes), Sm2SecureDigestAlgorithm.INSTANCE)
            .compact();
    }

    /**
     * 使用SM2公钥验证JWT Token
     */
    public static Map<String, Object> getToken(String token) {
        if (TiStrUtil.isEmpty(token)) {
            throw new TiUtilException("参数不合法");
        }
        return new TiJwtParser(false, false, null, 0L)
            .parse(token)
            .getPayload();
    }

    /**
     * 使用SM2公钥验证JWT Token
     */
    public static Map<String, Object> validToken(String token, byte[] publicKeyBytes) {
        if (TiStrUtil.isEmpty(token) || publicKeyBytes == null) {
            throw new TiUtilException("参数不合法");
        }
        return new TiJwtParser(true, true, publicKeyBytes, 0L)
            .parse(token)
            .getPayload();
    }

    /**
     * 使用SM2公钥验证JWT Token
     */
    public static Map<String, Object> validToken0(String token, byte[] publicKeyBytes) {
        if (TiStrUtil.isEmpty(token) || publicKeyBytes == null) {
            throw new TiUtilException("参数不合法");
        }
        try {
            return Jwts.parser()
                .sig()
                .add(Sm2SecureDigestAlgorithm.INSTANCE)
                .and()
                .verifyWith(TiSm2Util.loadPublicKey(publicKeyBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TiUtilException("Token过期", e);
        }
    }

    public static void test() {
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEjOfgpnPIXbPE+a5SSm+xv/Uu8N1Gh7QQmiIP2gTpER74M5YFpHNkrwa//IiXtpwBoA8v9mnZQqfE2Y44eYX6Qg==";
        String privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgc7V57xPTgMKaI2lrsvgGYYpWHd70vBXGlxawIXZZRaCgCgYIKoEcz1UBgi2hRANCAASM5+Cmc8hds8T5rlJKb7G/9S7w3UaHtBCaIg/aBOkRHvgzlgWkc2SvBr/8iJe2nAGgDy/2adlCp8TZjjh5hfpC";
        String token = generateTokenWithSM2(Map.of("test", true), TiBase64Util.decodeAsBytes(privateKey), 1000);
        System.out.println(token);
        System.out.println(validToken(token, TiBase64Util.decodeAsBytes(publicKey)));
        System.out.println(validToken0(token, TiBase64Util.decodeAsBytes(publicKey)));
        System.out.println(getToken(token));
    }

    public static void main(String[] args) {
        test();
    }

}
