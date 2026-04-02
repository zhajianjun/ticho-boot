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
 * JWT 工具类
 * <p>
 * 提供基于国密 SM2 算法的 JWT Token 生成、解析和验证功能
 * 使用非对称加密方式，确保 Token 的安全性和不可篡改性
 * </p>
 *
 * @author zhajianjun
 * @date 2025-12-27 14:28
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TiJwtUtil {
    /** JWT Header 中的类型标识 */
    private static final String TYP = "typ";
    /** JWT 类型值 */
    private static final String JWT = "JWT";
    /** JWT Token 分词器，用于解析 Token */
    private static final JwtTokenizer jwtTokenizer = new JwtTokenizer();
    /** JSON 反序列化器，用于解析 Header 和 Payload */
    private static final JacksonDeserializer<Map<String, ?>> deserialize = new JacksonDeserializer<>(new HashMap<>());

    /**
     * 使用 SM2 私钥生成 JWT Token（非对称加密）
     * <p>
     * 该方法使用国密 SM2 算法对 JWT 进行签名，生成安全的 Token。
     * Token 包含以下标准字段：
     * <ul>
     *     <li>jti: 随机生成的短 UUID，作为 Token 的唯一标识</li>
     *     <li>iat: Token 签发时间</li>
     *     <li>exp: Token 过期时间</li>
     * </ul>
     * </p>
     *
     * @param claims          token 数据，自定义的声明信息，不能为 null
     * @param privateKeyBytes 国密私钥字节数组，用于签名，不能为 null
     * @param expirationTime  有效期（毫秒），必须大于 0
     * @return 生成的 JWT Token 字符串
     * @throws TiUtilException 当参数不合法时抛出异常
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

    /**
     * 解析 JWT Token
     * <p>
     * 将 JWT Token 字符串解析为 {@link TiJwt} 对象，包含 Header、Payload 等信息。
     * 注意：此方法仅进行解析，不进行签名验证。
     * </p>
     *
     * @param token JWT Token 字符串
     * @return 解析后的 {@link TiJwt} 对象，包含 Token 的完整信息
     * @throws TiUtilException 当 Token 格式不正确时抛出异常
     */
    public static TiJwt parseToken(String token) {
        TokenizedJwt tokenized = getTokenize(token);
        Header header = getHeader(tokenized);
        Claims claims = getClaims(tokenized);
        return new TiJwt(token, tokenized, header, claims);
    }

    /**
     * 获取 Token 的分词对象
     * <p>
     * 使用 JwtTokenizer 将 Token 字符串解析为 TokenizedJwt 对象，
     * 该对象包含 Token 的三个部分：Protected Header、Payload、Signature
     * </p>
     *
     * @param token JWT Token 字符串
     * @return TokenizedJwt 对象，包含 Token 的各部分原始数据
     */
    private static TokenizedJwt getTokenize(String token) {
        Reader reader = new CharSequenceReader(token, 0, token.length());
        return jwtTokenizer.tokenize(reader);
    }

    /**
     * 解析并创建 JWT Header 对象
     * <p>
     * 从 TokenizedJwt 中获取 Protected Header 部分，进行 Base64 解码后反序列化为 Map，
     * 然后创建 Header 对象。Header 通常包含 alg（算法）和 typ（类型）等字段。
     * </p>
     *
     * @param tokenized TokenizedJwt 对象
     * @return 解析后的 Header 对象
     * @throws TiUtilException 当 Header 格式不正确或解析失败时抛出异常
     */
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
            throw new TiUtilException("无效的 JWT 请求头", e);
        }
    }

    /**
     * 解析 JWT Payload（Claims）
     * <p>
     * 从 TokenizedJwt 中获取 Payload 部分，进行 Base64 解码后反序列化为 Map，
     * 然后创建 DefaultClaims 对象。Claims 包含所有声明信息，如 jti、iat、exp 以及自定义字段。
     * </p>
     *
     * @param tokenized TokenizedJwt 对象
     * @return 解析后的 Claims 对象，包含所有声明信息
     */
    private static Claims getClaims(TokenizedJwt tokenized) {
        CharSequence payload = tokenized.getPayload();
        Map<String, ?> payloadMap = deserialize.deserialize(TiBase64Util.decodeAsBytes(payload));
        return new DefaultClaims(payloadMap);
    }

    /**
     * 测试方法
     * <p>
     * 演示完整的 JWT 生成、解析和验证流程：
     * <ol>
     *     <li>使用测试私钥生成 Token</li>
     *     <li>解析 Token</li>
     *     <li>使用测试公钥验证 Token 签名</li>
     *     <li>输出 Claims 信息</li>
     * </ol>
     * </p>
     * <p>
     * <b>安全警告</b>：此方法包含硬编码的测试密钥，仅供演示使用。
     * 生产环境必须从安全配置（如配置文件、密钥管理服务、环境变量）中获取密钥。
     * 建议使用 {@link top.ticho.tool.security.TiSm2Util#generateSM2KeyPair()} 生成新的密钥对。
     * </p>
     *
     */
    public static void test() {
        // 警告：以下密钥仅供测试使用，生产环境请使用自己的密钥对
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEjOfgpnPIXbPE+a5SSm+xv/Uu8N1Gh7QQmiIP2gTpER74M5YFpHNkrwa//IiXtpwBoA8v9mnZQqfE2Y44eYX6Qg==";
        String privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgc7V57xPTgMKaI2lrsvgGYYpWHd70vBXGlxawIXZZRaCgCgYIKoEcz1UBgi2hRANCAASM5+Cmc8hds8T5rlJKb7G/9S7w3UaHtBCaIg/aBOkRHvgzlgWkc2SvBr/8iJe2nAGgDy/2adlCp8TZjjh5hfpC";

        String token = generateTokenWithSM2(Map.of("test", true), TiBase64Util.decodeAsBytes(privateKey), 1000);
        System.out.println("生成的 Token: " + token);
        TiJwt tiJwt = parseToken(token);
        tiJwt.verify(TiBase64Util.decodeAsBytes(publicKey));
        System.out.println("验证结果: " + tiJwt.claims());
    }

    /**
     * 主方法
     * <p>
     * 程序入口，执行测试方法演示 JWT 功能
     * </p>
     */
    public static void main(String[] args) {
        test();
    }

}
