package top.ticho.tool.jwt;

import io.jsonwebtoken.security.SecureDigestAlgorithm;
import io.jsonwebtoken.security.SecureRequest;
import io.jsonwebtoken.security.VerifySecureDigestRequest;
import top.ticho.tool.core.exception.TiUtilException;
import top.ticho.tool.security.TiSm2Util;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-12-27 15:47
 */
class Sm2SecureDigestAlgorithm implements SecureDigestAlgorithm<PrivateKey, PublicKey> {
    public static final Sm2SecureDigestAlgorithm INSTANCE = new Sm2SecureDigestAlgorithm();

    @Override
    public String getId() {
        return TiSm2Util.SM2P256V1;
    }

    @Override
    public byte[] digest(SecureRequest<InputStream, PrivateKey> request) {
        InputStream payload = request.getPayload();
        byte[] bytes;
        try {
            bytes = payload.readAllBytes();
        } catch (IOException e) {
            throw new TiUtilException("签名异常", e);
        }
        return TiSm2Util.sign(bytes, request.getKey());
    }

    @Override
    public boolean verify(VerifySecureDigestRequest<PublicKey> request) {
        InputStream payload = request.getPayload();
        byte[] bytes;
        try {
            bytes = payload.readAllBytes();
        } catch (IOException e) {
            throw new TiUtilException("验签异常", e);
        }
        return TiSm2Util.verify(bytes, request.getDigest(), request.getKey());
    }


}
