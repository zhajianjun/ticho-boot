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
 * 基于 SM2 国密算法的安全摘要算法实现
 * <p>
 * 实现了 io.jsonwebtoken.security.SecureDigestAlgorithm 接口，用于 JWT 的签名和验签
 * 使用 SM2P256V1 曲线进行数字签名
 * </p>
 *
 * @author zhajianjun
 * @date 2025-12-27 15:47
 */
class Sm2SecureDigestAlgorithm implements SecureDigestAlgorithm<PrivateKey, PublicKey> {
    /**
     * 单例实例
     */
    public static final Sm2SecureDigestAlgorithm INSTANCE = new Sm2SecureDigestAlgorithm();

    /**
     * 获取算法标识符
     *
     * @return SM2 算法标识符 (SM2P256V1)
     */
    @Override
    public String getId() {
        return TiSm2Util.SM2P256V1;
    }

    /**
     * 对载荷进行数字签名
     * <p>
     * 从请求中读取输入流载荷，将其转换为字节数组，
     * 使用提供的私钥进行 SM2 签名
     * </p>
     *
     * @param request 安全请求，包含载荷输入流和签名私钥
     * @return 签名后的字节数组
     * @throws TiUtilException 当读取载荷发生 IO 异常时抛出
     */
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

    /**
     * 验证数字签名
     * <p>
     * 从请求中读取输入流载荷，将其转换为字节数组，
     * 使用提供的公钥验证签名与摘要是否匹配
     * </p>
     *
     * @param request 验证请求，包含载荷输入流、待验证的摘要和验签公钥
     * @return 验证结果，true 表示验证通过，false 表示验证失败
     * @throws TiUtilException 当读取载荷发生 IO 异常时抛出
     */
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
