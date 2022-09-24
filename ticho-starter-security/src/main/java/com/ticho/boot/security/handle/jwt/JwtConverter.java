package com.ticho.boot.security.handle.jwt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.util.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Objects;
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
        String encode = Base64.encode(publicKey.getEncoded());
        verifierKey = Arrays.stream(StrUtil.split(encode, 64)).collect(Collectors.joining("\n", "-----BEGIN PUBLIC KEY-----\n", "\n-----END PUBLIC KEY-----"));
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
        return key.startsWith("-----BEGIN PRIVATE");
    }

}