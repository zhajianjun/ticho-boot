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
    // ==================== 常量定义 ====================
    /** 椭圆曲线算法名称 */
    public static final String EC_ALGORITHM = "EC";
    /** Bouncy Castle提供者名称 */
    public static final String BC_PROVIDER = "BC";
    /** SM2标准曲线名称 */
    public static final String SM2P256V1 = "sm2p256v1";
    /** SM2签名算法OID */
    public static final String SM2_SIGN_ALGORITHM = GMObjectIdentifiers.sm2sign_with_sm3.toString();
    public static final ECDomainParameters ecDomainParameters;

    // ==================== 异常消息常量 ====================
    private static final String ERROR_UNKNOWN_ALGORITHM = "未知加密算法";
    private static final String ERROR_NO_PROVIDER = "无提供者";
    private static final String ERROR_INVALID_PARAMETER = "不合法的参数";
    private static final String ERROR_INVALID_KEY = "无效的密钥";
    private static final String ERROR_INVALID_CIPHER_TEXT = "无效的密文数据";
    private static final String ERROR_SIGNATURE_FAILED = "签名失败";
    private static final String ERROR_VERIFY_FAILED = "验证失败";
    private static final String ERROR_KEY_FORMAT = "密钥格式不正确";

    // ==================== 静态初始化 ====================
    static {
        Security.addProvider(new BouncyCastleProvider());
        X9ECParameters sm2ECParameters = ECNamedCurveTable.getByName(SM2P256V1);
        ecDomainParameters = new ECDomainParameters(
            sm2ECParameters.getCurve(),
            sm2ECParameters.getG(),
            sm2ECParameters.getN(),
            sm2ECParameters.getH()
        );
    }

    /**
     * 生成SM2密钥对
     *
     * @return {@link KeyPair} SM2密钥对
     * @throws TiUtilException 生成密钥对异常
     */
    public static KeyPair generateSM2KeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(EC_ALGORITHM, BC_PROVIDER);
            ECGenParameterSpec sm2Spec = new ECGenParameterSpec(SM2P256V1);
            generator.initialize(sm2Spec, new SecureRandom());
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("生成SM2密钥对异常，" + ERROR_UNKNOWN_ALGORITHM, e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("生成SM2密钥对异常，" + ERROR_NO_PROVIDER, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new TiUtilException("生成SM2密钥对异常，" + ERROR_INVALID_PARAMETER, e);
        }
    }

    /**
     * 打印生成的密钥对信息和测试加解密
     *
     * @param testData 测试数据
     */
    public static void printGeneratedKeyPair(String testData) {
        if (TiStrUtil.isEmpty(testData)) {
            throw new TiUtilException("测试数据不能为空");
        }
        KeyPair keyPair = generateSM2KeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String publicKeyStr = TiBase64Util.encode(publicKey.getEncoded());
        String privateKeyStr = TiBase64Util.encode(privateKey.getEncoded());
        // 测试加解密
        byte[] encryptData = encrypt(testData, publicKey.getEncoded());
        String encryptStr = TiBase64Util.encode(encryptData);
        byte[] decryptData = decrypt(encryptData, privateKey.getEncoded());
        System.out.println("==================== SM2密钥对信息 ====================");
        System.out.println("公钥：" + publicKeyStr);
        System.out.println("私钥：" + privateKeyStr);
        System.out.println("原始数据：" + testData);
        System.out.println("加密数据：" + encryptStr);
        System.out.println("解密数据：" + new String(decryptData, StandardCharsets.UTF_8));
        System.out.println("====================================================");
    }

    /**
     * SM2加密（Base64编码输入输出）
     *
     * @param data            明文数据
     * @param base64PublicKey Base64编码的公钥
     * @return {@link String} Base64编码的密文数据
     * @throws TiUtilException 加密异常
     */
    public static String encryptBase64(String data, String base64PublicKey) {
        if (TiStrUtil.isEmpty(data)) {
            return null;
        }
        if (TiStrUtil.isEmpty(base64PublicKey)) {
            throw new TiUtilException("公钥不能为空");
        }
        try {
            byte[] publicKeyBytes = TiBase64Util.decodeAsBytes(base64PublicKey);
            byte[] encryptBytes = encrypt(data, publicKeyBytes);
            return TiBase64Util.encode(encryptBytes);
        } catch (Exception e) {
            throw new TiUtilException("SM2加密失败", e);
        }
    }

    /**
     * SM2解密（Base64编码输入输出）
     *
     * @param encryptedDataBase64 Base64编码的密文数据
     * @param base64PrivateKey    Base64编码的私钥
     * @return {@link String} 明文数据
     * @throws TiUtilException 解密异常
     */
    public static String decryptBase64(String encryptedDataBase64, String base64PrivateKey) {
        if (TiStrUtil.isEmpty(encryptedDataBase64)) {
            return null;
        }
        if (TiStrUtil.isEmpty(base64PrivateKey)) {
            throw new TiUtilException("私钥不能为空");
        }
        try {
            byte[] encryptedDataBytes = TiBase64Util.decodeAsBytes(encryptedDataBase64);
            byte[] privateKeyBytes = TiBase64Util.decodeAsBytes(base64PrivateKey);
            byte[] decryptBytes = decrypt(encryptedDataBytes, privateKeyBytes);
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new TiUtilException("SM2解密失败", e);
        }
    }

    /**
     * SM2加密（字节数组输入输出）
     *
     * @param data           明文数据
     * @param publicKeyBytes 公钥字节数组
     * @return {@link byte[]} 密文字节数组
     * @throws TiUtilException 加密异常
     */
    public static byte[] encrypt(String data, byte[] publicKeyBytes) {
        if (TiStrUtil.isEmpty(data)) {
            throw new TiUtilException("明文数据不能为空");
        }
        if (publicKeyBytes == null || publicKeyBytes.length == 0) {
            throw new TiUtilException("公钥字节数组不能为空");
        }
        try {
            PublicKey publicKey = loadPublicKey(publicKeyBytes);
            BCECPublicKey ecPublicKey = (BCECPublicKey) publicKey;
            SM2Engine engine = new SM2Engine();
            engine.init(true, new ParametersWithRandom(new ECPublicKeyParameters(ecPublicKey.getQ(), ecDomainParameters), new SecureRandom()));
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            return engine.processBlock(dataBytes, 0, dataBytes.length);
        } catch (InvalidCipherTextException e) {
            throw new TiUtilException("加密异常，" + ERROR_INVALID_CIPHER_TEXT, e);
        } catch (ClassCastException e) {
            throw new TiUtilException("加密异常，公钥类型不正确", e);
        }
    }

    /**
     * SM2解密（字节数组输入输出）
     *
     * @param encryptedDataBytes 密文字节数组
     * @param privateKeyBytes    私钥字节数组
     * @return {@link byte[]} 明文数据字节数组
     * @throws TiUtilException 解密异常
     */
    public static byte[] decrypt(byte[] encryptedDataBytes, byte[] privateKeyBytes) {
        if (encryptedDataBytes == null || encryptedDataBytes.length == 0) {
            throw new TiUtilException("密文字节数组不能为空");
        }
        if (privateKeyBytes == null || privateKeyBytes.length == 0) {
            throw new TiUtilException("私钥字节数组不能为空");
        }
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyBytes);
            BCECPrivateKey ecPrivateKey = (BCECPrivateKey) privateKey;

            SM2Engine engine = new SM2Engine();
            engine.init(false, new ECPrivateKeyParameters(ecPrivateKey.getD(), ecDomainParameters));
            return engine.processBlock(encryptedDataBytes, 0, encryptedDataBytes.length);
        } catch (InvalidCipherTextException | IllegalArgumentException e) {
            throw new TiUtilException("解密异常，" + ERROR_INVALID_CIPHER_TEXT, e);
        } catch (ClassCastException e) {
            throw new TiUtilException("解密异常，私钥类型不正确", e);
        }
    }

    /**
     * SM2签名（使用PrivateKey对象）
     *
     * @param data       待签名的数据
     * @param privateKey 私钥对象
     * @return {@link byte[]} 签名字节数组
     * @throws TiUtilException 签名异常
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) {
        if (data == null || data.length == 0) {
            throw new TiUtilException("待签名数据不能为空");
        }
        if (privateKey == null) {
            throw new TiUtilException("私钥对象不能为空");
        }
        try {
            Signature signature = Signature.getInstance(SM2_SIGN_ALGORITHM, BC_PROVIDER);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("签名异常，" + ERROR_UNKNOWN_ALGORITHM, e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("签名异常，" + ERROR_NO_PROVIDER, e);
        } catch (InvalidKeyException e) {
            throw new TiUtilException("签名异常，" + ERROR_INVALID_KEY, e);
        } catch (SignatureException e) {
            throw new TiUtilException("签名异常，" + ERROR_SIGNATURE_FAILED, e);
        }
    }

    /**
     * SM2签名（字节数组数据）
     *
     * @param data            待签名的数据
     * @param privateKeyBytes 私钥字节数组
     * @return {@link byte[]} 签名字节数组
     * @throws TiUtilException 签名异常
     */
    public static byte[] sign(byte[] data, byte[] privateKeyBytes) {
        if (data == null || data.length == 0) {
            throw new TiUtilException("待签名数据不能为空");
        }
        if (privateKeyBytes == null || privateKeyBytes.length == 0) {
            throw new TiUtilException("私钥字节数组不能为空");
        }
        PrivateKey privateKey = loadPrivateKey(privateKeyBytes);
        return sign(data, privateKey);
    }

    /**
     * SM2签名（字符串数据，Base64输出）
     *
     * @param data             待签名的数据
     * @param base64PrivateKey Base64编码的私钥
     * @return {@link String} Base64编码的签名
     * @throws TiUtilException 签名异常
     */
    public static String signBase64(String data, String base64PrivateKey) {
        if (TiStrUtil.isEmpty(data)) {
            throw new TiUtilException("待签名数据不能为空");
        }
        if (TiStrUtil.isEmpty(base64PrivateKey)) {
            throw new TiUtilException("私钥不能为空");
        }
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] privateKeyBytes = TiBase64Util.decodeAsBytes(base64PrivateKey);
            byte[] signBytes = sign(dataBytes, privateKeyBytes);
            return TiBase64Util.encode(signBytes);
        } catch (Exception e) {
            throw new TiUtilException("SM2签名失败", e);
        }
    }

    /**
     * SM2验签（使用PublicKey对象）
     *
     * @param data      待验证的数据
     * @param sign      签名字节数组
     * @param publicKey 公钥对象
     * @return {@link boolean} 验证结果
     * @throws TiUtilException 验签异常
     */
    public static boolean verify(byte[] data, byte[] sign, PublicKey publicKey) {
        if (data == null || data.length == 0) {
            throw new TiUtilException("待验证数据不能为空");
        }
        if (sign == null || sign.length == 0) {
            throw new TiUtilException("签名字节数组不能为空");
        }
        if (publicKey == null) {
            throw new TiUtilException("公钥对象不能为空");
        }
        try {
            Signature signature = Signature.getInstance(SM2_SIGN_ALGORITHM, BC_PROVIDER);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("验签异常，" + ERROR_UNKNOWN_ALGORITHM, e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("验签异常，" + ERROR_NO_PROVIDER, e);
        } catch (InvalidKeyException e) {
            throw new TiUtilException("验签异常，" + ERROR_INVALID_KEY, e);
        } catch (SignatureException e) {
            throw new TiUtilException("验签异常，" + ERROR_VERIFY_FAILED, e);
        }
    }

    /**
     * SM2验签（字节数组数据）
     *
     * @param data           待验证的数据
     * @param sign           签名字节数组
     * @param publicKeyBytes 公钥字节数组
     * @return {@link boolean} 验证结果
     * @throws TiUtilException 验签异常
     */
    public static boolean verify(byte[] data, byte[] sign, byte[] publicKeyBytes) {
        if (data == null || data.length == 0) {
            throw new TiUtilException("待验证数据不能为空");
        }
        if (sign == null || sign.length == 0) {
            throw new TiUtilException("签名字节数组不能为空");
        }
        if (publicKeyBytes == null || publicKeyBytes.length == 0) {
            throw new TiUtilException("公钥字节数组不能为空");
        }
        PublicKey publicKey = loadPublicKey(publicKeyBytes);
        return verify(data, sign, publicKey);
    }

    /**
     * SM2验签（字符串数据，Base64输入）
     *
     * @param data            待验证的数据
     * @param base64Sign      Base64编码的签名
     * @param base64PublicKey Base64编码的公钥
     * @return {@link boolean} 验证结果
     * @throws TiUtilException 验签异常
     */
    public static boolean verifyBase64(String data, String base64Sign, String base64PublicKey) {
        if (TiStrUtil.isEmpty(data)) {
            throw new TiUtilException("待验证数据不能为空");
        }
        if (TiStrUtil.isEmpty(base64Sign)) {
            throw new TiUtilException("签名不能为空");
        }
        if (TiStrUtil.isEmpty(base64PublicKey)) {
            throw new TiUtilException("公钥不能为空");
        }
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] signBytes = TiBase64Util.decodeAsBytes(base64Sign);
            byte[] publicKeyBytes = TiBase64Util.decodeAsBytes(base64PublicKey);
            return verify(dataBytes, signBytes, publicKeyBytes);
        } catch (Exception e) {
            throw new TiUtilException("SM2验签失败", e);
        }
    }

    /**
     * 加载私钥（PKCS8格式）
     *
     * @param privateKeyBytes 私钥字节数组
     * @return {@link PrivateKey} 私钥对象
     * @throws TiUtilException 加载私钥异常
     */
    public static PrivateKey loadPrivateKey(byte[] privateKeyBytes) {
        if (privateKeyBytes == null || privateKeyBytes.length == 0) {
            throw new TiUtilException("私钥字节数组不能为空");
        }
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(EC_ALGORITHM, BC_PROVIDER);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("加载私钥异常，" + ERROR_UNKNOWN_ALGORITHM, e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("加载私钥异常，" + ERROR_NO_PROVIDER, e);
        } catch (InvalidKeySpecException e) {
            throw new TiUtilException("加载私钥异常，" + ERROR_KEY_FORMAT, e);
        }
    }

    /**
     * 加载公钥（X509格式）
     *
     * @param publicKeyBytes 公钥字节数组
     * @return {@link PublicKey} 公钥对象
     * @throws TiUtilException 加载公钥异常
     */
    public static PublicKey loadPublicKey(byte[] publicKeyBytes) {
        if (publicKeyBytes == null || publicKeyBytes.length == 0) {
            throw new TiUtilException("公钥字节数组不能为空");
        }
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(EC_ALGORITHM, BC_PROVIDER);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("加载公钥异常，" + ERROR_UNKNOWN_ALGORITHM, e);
        } catch (NoSuchProviderException e) {
            throw new TiUtilException("加载公钥异常，" + ERROR_NO_PROVIDER, e);
        } catch (InvalidKeySpecException e) {
            throw new TiUtilException("加载公钥异常，" + ERROR_KEY_FORMAT, e);
        }
    }

    /**
     * 主方法 - 用于测试
     */
    public static void main(String[] args) {
        // 测试生成密钥对
        printGeneratedKeyPair("Hello SM2!");
        // 测试预设密钥的加解密
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEjOfgpnPIXbPE+a5SSm+xv/Uu8N1Gh7QQmiIP2gTpER74M5YFpHNkrwa//IiXtpwBoA8v9mnZQqfE2Y44eYX6Qg==";
        String privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgc7V57xPTgMKaI2lrsvgGYYpWHd70vBXGlxawIXZZRaCgCgYIKoEcz1UBgi2hRANCAASM5+Cmc8hds8T5rlJKb7G/9S7w3UaHtBCaIg/aBOkRHvgzlgWkc2SvBr/8iJe2nAGgDy/2adlCp8TZjjh5hfpC";
        String testData = "test1234";
        try {
            String encrypted = encryptBase64(testData, publicKey);
            System.out.println("加密结果: " + encrypted);
            String decrypted = decryptBase64(encrypted, privateKey);
            System.out.println("解密结果: " + decrypted);
            // 测试签名验签
            String signature = signBase64(testData, privateKey);
            System.out.println("签名结果: " + signature);
            boolean verifyResult = verifyBase64(testData, signature, publicKey);
            System.out.println("验签结果: " + verifyResult);
        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
