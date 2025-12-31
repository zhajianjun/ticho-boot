package top.ticho.tool.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwt;
import io.jsonwebtoken.impl.JwtTokenizer;
import io.jsonwebtoken.impl.TokenizedJwt;
import io.jsonwebtoken.impl.io.AbstractParser;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Strings;
import lombok.AllArgsConstructor;
import top.ticho.tool.core.TiBase64Util;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.exception.TiUtilException;
import top.ticho.tool.security.TiSm2Util;

import java.io.Reader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-12-28 10:11
 */
@AllArgsConstructor
public class TiJwtParser extends AbstractParser<Jwt<Header, Claims>> {
    private static final JwtTokenizer jwtTokenizer = new JwtTokenizer();
    private static final JacksonDeserializer<Map<String, ?>> deserialize = new JacksonDeserializer<>(new HashMap<>());

    private final boolean validAlgorithm;
    private final boolean validExpiration;
    private final byte[] publicKeyBytes;
    private final long allowedClockSkewMillis;

    /**
     * 解析
     *
     * @param reader token数据
     */
    @Override
    public Jwt<Header, Claims> parse(Reader reader) {
        TokenizedJwt tokenized = jwtTokenizer.tokenize(reader);
        Header header = getHeader(tokenized);
        Claims claims = getClaims(tokenized);
        validateAlgorithm(header, tokenized);
        validateExpectedClaims(claims);
        return new DefaultJwt<>(header, claims);
    }

    private static Claims getClaims(TokenizedJwt tokenized) {
        CharSequence payload = tokenized.getPayload();
        Map<String, ?> payloadMap = deserialize.deserialize(TiBase64Util.decodeAsBytes(payload));
        return new DefaultClaims(payloadMap);
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

    public void validateAlgorithm(Header header, TokenizedJwt tokenized) {
        if (!validAlgorithm) {
            return;
        }
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

    public void validateExpectedClaims(Claims claims) {
        if (!validExpiration) {
            return;
        }
        boolean allowSkew = allowedClockSkewMillis > 0;
        Date now = new Date();
        long nowTime = now.getTime();
        Date exp = claims.getExpiration();
        if (exp != null) {
            long maxTime = nowTime - allowedClockSkewMillis;
            Date max = allowSkew ? new Date(maxTime) : now;
            if (max.after(exp)) {
                throw new TiUtilException("Token过期");
            }
        }
    }

}
