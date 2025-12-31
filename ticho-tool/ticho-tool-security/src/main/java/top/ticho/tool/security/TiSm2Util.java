package top.ticho.tool.security;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import top.ticho.tool.core.TiBase64Util;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.exception.TiUtilException;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 国密2加密
 *
 * @author zhajianjun
 * @date 2025-12-27 11:44
 */
public class TiSm2Util {
    public static final String EC = "EC";
    public static final String BC = "BC";
    public static final String SM2P256V1 = "sm2p256v1";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成SM2密钥对
     */
    public static KeyPair generateSM2KeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(EC, BC);
            ECGenParameterSpec sm2Spec = new ECGenParameterSpec(SM2P256V1);
            generator.initialize(sm2Spec);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("生成SM2密钥对异常，未知加密算法", e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("生成SM2密钥对异常，无提供者", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new TiUtilException("生成SM2密钥对异常，不合法的参数", e);
        }
    }

    public static void printGeneratedKeyPair(String validData) {
        KeyPair keyPair = generateSM2KeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String publicKeyStr = TiBase64Util.encode(publicKey.getEncoded());
        String privateKeyStr = TiBase64Util.encode(privateKey.getEncoded());
        byte[] encrypt = encrypt(validData, TiBase64Util.decodeAsBytes(publicKeyStr));
        String encryptStr = TiBase64Util.encode(encrypt);
        byte[] decrypt = decrypt(TiBase64Util.decodeAsBytes(encryptStr), TiBase64Util.decodeAsBytes(privateKeyStr));
        System.out.println("公钥：" + publicKeyStr);
        System.out.println("私钥：" + privateKeyStr);
        System.out.println("加密数据：" + encryptStr);
        System.out.println("解密数据：" + new String(decrypt));
    }

    /**
     * SM2加密
     *
     * @param data            数据
     * @param base64PublicKey 公钥 需base64解密
     * @return {@link String } 密文数据 base64加密
     */
    public static String encryptBase64(String data, String base64PublicKey) {
        if (TiStrUtil.isEmpty(data)) {
            return null;
        }
        byte[] publicKeyBytes = TiBase64Util.decodeAsBytes(base64PublicKey);
        byte[] encrypt = encrypt(data, publicKeyBytes);
        return TiBase64Util.encode(encrypt);
    }

    /**
     * SM2解密
     *
     * @param encryptedDataBase64 密文数据 需base64解密
     * @param base64PrivateKey    私钥 需base64解密
     * @return {@link String } 密文原数据
     */
    public static String decryptBase64(String encryptedDataBase64, String base64PrivateKey) {
        if (TiStrUtil.isEmpty(encryptedDataBase64)) {
            return null;
        }
        byte[] dataBytes = TiBase64Util.decodeAsBytes(encryptedDataBase64);
        byte[] privateKeyBytes = TiBase64Util.decodeAsBytes(base64PrivateKey);
        byte[] encrypt = decrypt(dataBytes, privateKeyBytes);
        return new String(encrypt, StandardCharsets.UTF_8);
    }

    /**
     * SM2加密
     *
     * @param data           数据
     * @param publicKeyBytes 公钥字节
     * @return {@link byte[] }
     */
    public static byte[] encrypt(String data, byte[] publicKeyBytes) {
        try {
            byte[] dataBytes = data.getBytes();
            PublicKey publicKey = loadPublicKey(publicKeyBytes);
            BCECPublicKey ecPublicKey = (BCECPublicKey) publicKey;
            ECPoint ecPoint = ecPublicKey.getQ();
            ECDomainParameters domainParameters = getECDomainParameters();
            SM2Engine engine = new SM2Engine();
            engine.init(true, new ParametersWithRandom(new ECPublicKeyParameters(ecPoint, domainParameters), new SecureRandom()));
            return engine.processBlock(dataBytes, 0, dataBytes.length);
        } catch (InvalidCipherTextException e) {
            throw new TiUtilException("加密异常，无效的密文数据", e);
        }
    }

    /**
     * SM2解密
     *
     * @param encryptedDataBytes 密文数据
     * @param privateKeyBytes    私钥字节
     * @return {@link byte[] }
     */
    public static byte[] decrypt(byte[] encryptedDataBytes, byte[] privateKeyBytes) {
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyBytes);
            BCECPrivateKey ecPrivateKey = (BCECPrivateKey) privateKey;
            ECDomainParameters domainParameters = getECDomainParameters();
            SM2Engine engine = new SM2Engine();
            engine.init(false, new ECPrivateKeyParameters(ecPrivateKey.getD(), domainParameters));
            return engine.processBlock(encryptedDataBytes, 0, encryptedDataBytes.length);
        } catch (InvalidCipherTextException | IllegalArgumentException e) {
            throw new TiUtilException("解密异常，无效的密文数据", e);
        }
    }

    /**
     * SM2签名
     */
    public static byte[] sign(byte[] data, byte[] privateKeyBytes) {
        PrivateKey privateKey = loadPrivateKey(privateKeyBytes);
        return sign(data, privateKey);
    }

    /**
     * SM2签名
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), BC);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("签名异常，未知加密算法", e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("签名异常，无提供者", e);
        } catch (InvalidKeyException e) {
            throw new TiUtilException("签名异常，未知公钥", e);
        } catch (SignatureException e) {
            throw new TiUtilException("签名异常，签名失败", e);
        }
    }

    /**
     * SM2验签
     */
    public static boolean verify(byte[] data, byte[] sign, byte[] publicKeyBytes) {
        PublicKey publicKey = loadPublicKey(publicKeyBytes);
        return verify(data, sign, publicKey);
    }

    /**
     * SM2验签
     */
    public static boolean verify(byte[] data, byte[] sign, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), "BC");
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("验签异常，未知加密算法", e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("验签异常，无提供者", e);
        } catch (InvalidKeyException e) {
            throw new TiUtilException("验签异常，未知公钥", e);
        } catch (SignatureException e) {
            throw new TiUtilException("验签异常，验证失败", e);
        }
    }

    /**
     * 加载私钥
     */
    public static PrivateKey loadPrivateKey(byte[] privateKeyBytes) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("加载私钥异常，未知加密算法", e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("加载私钥异常，无提供者", e);
        } catch (InvalidKeySpecException e) {
            throw new TiUtilException("加载私钥异常，秘钥格式不正确", e);
        }
    }

    /**
     * 加载公钥
     */
    public static PublicKey loadPublicKey(byte[] publicKeyBytes) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("加载公钥异常，未知加密算法", e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("加载公钥异常，无提供者", e);
        } catch (InvalidKeySpecException e) {
            throw new TiUtilException("加载公钥异常，秘钥格式不正确", e);
        }
    }

    private static ECDomainParameters getECDomainParameters() {
        X9ECParameters sm2ECParameters = ECNamedCurveTable.getByName("sm2p256v1");
        return new ECDomainParameters(
            sm2ECParameters.getCurve(),
            sm2ECParameters.getG(),
            sm2ECParameters.getN(),
            sm2ECParameters.getH());
    }

    public static void main(String[] args) {
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEjOfgpnPIXbPE+a5SSm+xv/Uu8N1Gh7QQmiIP2gTpER74M5YFpHNkrwa//IiXtpwBoA8v9mnZQqfE2Y44eYX6Qg==";
        String privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgc7V57xPTgMKaI2lrsvgGYYpWHd70vBXGlxawIXZZRaCgCgYIKoEcz1UBgi2hRANCAASM5+Cmc8hds8T5rlJKb7G/9S7w3UaHtBCaIg/aBOkRHvgzlgWkc2SvBr/8iJe2nAGgDy/2adlCp8TZjjh5hfpC";
        String encryptBase64 = encryptBase64("test1234", publicKey);
        System.out.println(encryptBase64);
        System.out.println(decryptBase64(encryptBase64, privateKey));
    }

}
