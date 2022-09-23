package com.ticho.boot.security.handle.jwt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.dto.OAuth2AccessToken;
import com.ticho.boot.security.dto.SecurityUser;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.exception.BizException;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.JsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * jwt转换器
 *
 * @author zhajianjun
 * @date 2022-09-21 15:55
 */
@Slf4j
@Getter
public class JwtConverter implements InitializingBean {
    public static final String tokenCheckFail = "token验证失败";


    private String verifierKey;

    private final Signer signer;

    private String signingKey;

    private SignatureVerifier verifier;

    @Override
    public void afterPropertiesSet() {
        if (verifier != null) {
            // Assume signer also set independently if needed
            return;
        }
        SignatureVerifier verifier = new MacSigner(verifierKey);
        try {
            verifier = new RsaVerifier(verifierKey);
        } catch (Exception e) {
            log.warn("Unable to create an RSA verifier from verifierKey (ignoreable if using MAC)");
        }
        // Check the signing and verification keys match
        if (signer instanceof RsaSigner) {
            byte[] test = "test".getBytes();
            try {
                verifier.verify(test, signer.sign(test));
                log.info("Signing and verification RSA keys match");
            } catch (InvalidSignatureException e) {
                log.error("Signing and verification RSA keys do not match");
            }
        } else if (verifier instanceof MacSigner) {
            // Avoid a race condition where setters are called in the wrong order. Use of
            // == is intentional.
            Assert.isTrue(Objects.equals(this.signingKey, this.verifierKey), BizErrCode.FAIL,
                    "For MAC signing you do not need to specify the verifier key separately, and if you do it must match the signing key");
        }
        this.verifier = verifier;
    }

    /**
     * 通过证书加密
     */
    public JwtConverter(String path, String alias, String password) {
        // @formatter:off
        ClassPathResource resource = new ClassPathResource(path);
        //设置密钥对（私钥） 此处传入的是创建jks文件时的别名-alias 和 秘钥库访问密码
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, password.toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);
        PrivateKey privateKey = keyPair.getPrivate();
        Assert.isTrue(privateKey instanceof RSAPrivateKey, BizErrCode.FAIL, "KeyPair must be an RSA ");
        signer = new RsaSigner((RSAPrivateKey) privateKey);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        verifier = new RsaVerifier(publicKey);
        verifierKey = "-----BEGIN PUBLIC KEY-----\n" + Base64.encode(publicKey.getEncoded()) + "\n-----END PUBLIC KEY-----";
        // @formatter:on
    }

    /**
     * 通过公钥加密
     */
    public JwtConverter(String key) {
        key = key.trim();
        this.signingKey = key;
        if (isPublic(key)) {
            signer = new RsaSigner(key);
            log.info("Configured with RSA signing key");
        } else {
            // Assume it's a MAC key
            this.verifierKey = key;
            signer = new MacSigner(key);
        }
    }

    public boolean isPublic(String key) {
        return key.startsWith("-----BEGIN");
    }

    public void encode(OAuth2AccessToken oAuth2AccessToken, SecurityUser securityUser) {
        // @formatter:off
        Assert.isNotNull(signer, BizErrCode.FAIL, "signer is null");
        long iat = System.currentTimeMillis();
        long millis1 = Duration.ofHours(1).toMillis();
        long millis2 = Duration.ofHours(2).toMillis();
        Long exp = iat + millis1;
        Long refreTokenExp = iat + millis2;
        oAuth2AccessToken.setIat(iat);
        oAuth2AccessToken.setExp(exp);
        oAuth2AccessToken.setExpiresIn(oAuth2AccessToken.getExpiresIn());
        List<String> authorities = securityUser.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        Map<String, Object> accessTokenInfo = new HashMap<>();
        accessTokenInfo.put(OAuth2Const.TYPE, OAuth2Const.ACCESS_TOKEN);
        accessTokenInfo.put(OAuth2Const.EXP, exp);
        accessTokenInfo.put(OAuth2Const.USERNAME, securityUser.getUsername());
        accessTokenInfo.put(OAuth2Const.AUTHORITIES, authorities);
        accessTokenInfo.put(OAuth2Const.STATUS, securityUser.getStatus());
        Map<String, Object> extInfo = oAuth2AccessToken.getExtInfo();
        if (!CollectionUtils.isEmpty(extInfo)) {
            accessTokenInfo.putAll(oAuth2AccessToken.getExtInfo());
        }
        Map<String, Object> refreshTokenInfo = new HashMap<>();
        refreshTokenInfo.put(OAuth2Const.TYPE, OAuth2Const.REFRESH_TOKEN);
        refreshTokenInfo.put(OAuth2Const.EXP, refreTokenExp);
        refreshTokenInfo.put(OAuth2Const.USERNAME, securityUser.getUsername());
        String accessToken = JwtHelper.encode(JsonUtil.toJsonString(accessTokenInfo), signer).getEncoded();
        String refreshToken = JwtHelper.encode(JsonUtil.toJsonString(refreshTokenInfo), signer).getEncoded();
        oAuth2AccessToken.setAccessToken(accessToken);
        oAuth2AccessToken.setRefreshToken(refreshToken);
        // @formatter:on
    }

    public Map<String, Object> decode(String token) {
        String claims = JwtHelper.decode(token).getClaims();
        return JsonUtil.toMap(claims, String.class, Object.class);
    }

    public Map<String, Object> decodeAndVerify(String token) {
        String claims;
        try {
            claims = JwtHelper.decodeAndVerify(token, verifier).getClaims();
        } catch (Exception e) {
            log.error("token验证失败, {}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, tokenCheckFail);
        }
        Assert.isNotNull(claims, BizErrCode.FAIL, tokenCheckFail);
        Map<String, Object> map = JsonUtil.toMap(claims, String.class, Object.class);
        boolean isExpired = false;
        if (CollUtil.isEmpty(map) || !map.containsKey(OAuth2Const.EXP)) {
            isExpired = true;
        }
        if (!isExpired) {
            String numberStr = Optional.ofNullable(map.get(OAuth2Const.EXP)).map(Object::toString).orElse(null);
            BigDecimal ext = NumberUtil.toBigDecimal(numberStr);
            isExpired = ext.longValue() < System.currentTimeMillis();
        }
        Assert.isTrue(!isExpired, BizErrCode.FAIL, "token过期");
        return map;
    }

}
