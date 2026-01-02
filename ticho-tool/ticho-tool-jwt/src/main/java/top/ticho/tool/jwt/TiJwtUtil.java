package top.ticho.tool.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.JwtTokenizer;
import io.jsonwebtoken.impl.TokenizedJwt;
import io.jsonwebtoken.impl.io.CharSequenceReader;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import lombok.NoArgsConstructor;
import top.ticho.tool.core.TiBase64Util;
import top.ticho.tool.core.TiIdUtil;
import top.ticho.tool.core.exception.TiUtilException;
import top.ticho.tool.security.TiSm2Util;

import java.io.Reader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author zhajianjun
 * @date 2025-12-27 14:28
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TiJwtUtil {
    private static final String TYP = "typ";
    private static final String JWT = "JWT";
    private static final JwtTokenizer jwtTokenizer = new JwtTokenizer();
    private static final JacksonDeserializer<Map<String, ?>> deserialize = new JacksonDeserializer<>(new HashMap<>());

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
            .id(TiIdUtil.shortUuid())
            .expiration(expiration)
            .signWith(TiSm2Util.loadPrivateKey(privateKeyBytes), Sm2SecureDigestAlgorithm.INSTANCE)
            .compact();
    }

    public static TiJwt parseToken(String token) {
        TokenizedJwt tokenized = getTokenize(token);
        Header header = getHeader(tokenized);
        Claims claims = getClaims(tokenized);
        return new TiJwt(token, tokenized, header, claims);
    }

    private static TokenizedJwt getTokenize(String token) {
        Reader reader = new CharSequenceReader(token, 0, token.length());
        return jwtTokenizer.tokenize(reader);
    }

    private static Header getHeader(TokenizedJwt tokenized) {
        CharSequence protectedHeader = tokenized.getProtected();
        byte[] headerBytes = TiBase64Util.decodeAsBytes(protectedHeader);
        Map<String, ?> headerMap;
        if (headerBytes == null) {
            headerMap = Collections.emptyMap();
        } else {
            headerMap = deserialize.deserialize(headerBytes);
        }
        try {
            return tokenized.createHeader(headerMap);
        } catch (Exception e) {
            throw new TiUtilException("无效的JWT请求头", e);
        }
    }

    private static Claims getClaims(TokenizedJwt tokenized) {
        CharSequence payload = tokenized.getPayload();
        Map<String, ?> payloadMap = deserialize.deserialize(TiBase64Util.decodeAsBytes(payload));
        return new DefaultClaims(payloadMap);
    }

    public static void test() {
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEjOfgpnPIXbPE+a5SSm+xv/Uu8N1Gh7QQmiIP2gTpER74M5YFpHNkrwa//IiXtpwBoA8v9mnZQqfE2Y44eYX6Qg==";
        String privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgc7V57xPTgMKaI2lrsvgGYYpWHd70vBXGlxawIXZZRaCgCgYIKoEcz1UBgi2hRANCAASM5+Cmc8hds8T5rlJKb7G/9S7w3UaHtBCaIg/aBOkRHvgzlgWkc2SvBr/8iJe2nAGgDy/2adlCp8TZjjh5hfpC";
        String token = generateTokenWithSM2(Map.of("test", true), TiBase64Util.decodeAsBytes(privateKey), 1000);
        System.out.println(token);
        TiJwt tiJwt = parseToken(token);
        tiJwt.verify(TiBase64Util.decodeAsBytes(publicKey));
        System.out.println(tiJwt.claims());
    }

    public static void main(String[] args) {
        test();
    }

}
