package top.ticho.tool.security;

import top.ticho.tool.core.TiBase64Util;
import top.ticho.tool.core.exception.TiUtilException;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具类
 *
 * @author zhajianjun
 * @date 2026-01-25 12:49
 */
public class TiRsaUtil {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    /** 密钥长度 */
    private static final int DEFAULT_KEY_SIZE = 2048;


    /**
     * 生成密钥对
     */
    public static void printGeneratedKeyPair(String validData) {
        KeyPair keyPair = generateRsaKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String publicKeyStr = TiBase64Util.encode(publicKey.getEncoded());
        String privateKeyStr = TiBase64Util.encode(privateKey.getEncoded());
        String encrypt = encryptByPublicKey(validData, publicKeyStr);
        String decrypt = decryptByPrivateKey(encrypt, privateKeyStr);
        System.out.println("公钥：" + publicKeyStr);
        System.out.println("私钥：" + privateKeyStr);
        System.out.println("加密数据：" + encrypt);
        System.out.println("解密数据：" + decrypt);
    }

    private static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(DEFAULT_KEY_SIZE, new SecureRandom());
            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new TiUtilException("生成RSA密钥对失败，未知加密算法", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥(Base64编码)
     * @return 加密后的数据(Base64编码)
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        return encryptByPublicKey(data.getBytes(), publicKey);
    }

    /**
     * 公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥(Base64编码)
     * @return 加密后的数据(Base64编码)
     */
    public static String encryptByPublicKey(byte[] data, String publicKey) {
        try {
            byte[] keyBytes = TiBase64Util.decodeAsBytes(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedData = cipher.doFinal(data);

            return TiBase64Util.encode(encryptedData);
        } catch (Exception e) {
            throw new TiUtilException("RSA公钥加密失败", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密的数据(Base64编码)
     * @param privateKey    私钥(Base64编码)
     * @return 解密后的数据
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKey) {
        try {
            byte[] encryptedBytes = TiBase64Util.decodeAsBytes(encryptedData);
            byte[] keyBytes = TiBase64Util.decodeAsBytes(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] decryptedData = cipher.doFinal(encryptedBytes);

            return new String(decryptedData);
        } catch (Exception e) {
            throw new TiUtilException("RSA私钥解密失败", e);
        }
    }

    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 私钥(Base64编码)
     * @return 加密后的数据(Base64编码)
     */
    public static String encryptByPrivateKey(String data, String privateKey) {
        return encryptByPrivateKey(data.getBytes(), privateKey);
    }

    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 私钥(Base64编码)
     * @return 加密后的数据(Base64编码)
     */
    public static String encryptByPrivateKey(byte[] data, String privateKey) {
        try {
            byte[] keyBytes = TiBase64Util.decodeAsBytes(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, priKey);
            byte[] encryptedData = cipher.doFinal(data);

            return TiBase64Util.encode(encryptedData);
        } catch (Exception e) {
            throw new TiUtilException("RSA私钥加密失败", e);
        }
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密的数据(Base64编码)
     * @param publicKey     公钥(Base64编码)
     * @return 解密后的数据
     */
    public static String decryptByPublicKey(String encryptedData, String publicKey) {
        try {
            byte[] encryptedBytes = TiBase64Util.decodeAsBytes(encryptedData);
            byte[] keyBytes = TiBase64Util.decodeAsBytes(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            byte[] decryptedData = cipher.doFinal(encryptedBytes);

            return new String(decryptedData);
        } catch (Exception e) {
            throw new TiUtilException("RSA公钥解密失败", e);
        }
    }

    /**
     * 使用私钥对数据进行签名
     *
     * @param data       待签名的数据
     * @param privateKey 私钥(Base64编码)
     * @return 签名值(Base64编码)
     */
    public static String sign(String data, String privateKey) {
        try {
            byte[] keyBytes = TiBase64Util.decodeAsBytes(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(data.getBytes());
            byte[] sign = signature.sign();

            return TiBase64Util.encode(sign);
        } catch (Exception e) {
            throw new TiUtilException("RSA签名失败", e);
        }
    }

    /**
     * 使用公钥验证签名
     *
     * @param data      待验证的数据
     * @param publicKey 公钥(Base64编码)
     * @param sign      签名值(Base64编码)
     * @return 验证结果
     */
    public static boolean verify(String data, String publicKey, String sign) {
        try {
            byte[] keyBytes = TiBase64Util.decodeAsBytes(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(data.getBytes());
            return signature.verify(TiBase64Util.decodeAsBytes(sign));
        } catch (Exception e) {
            throw new TiUtilException("RSA验证签名失败", e);
        }
    }

    public static void main(String[] args) {
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApyWb2guBpym3E2bkKPzM4WiZ6uVWbXm8feWp7KOtFV3US+oEzQ8I6l514GcSVw2eyVGdQXF16EcCkzt2U7Twqcbtkqe4r3/tfn2lFt3giuCWndRSIU/vwgXzWlvLDw+BNLkb/XDtjtoAu4jE9Pd+UFeOR2BzY9vz6h/Y8uepwf7rpyJK0YibAyTm1Kg4ThNcijvJMS6vVegYRjv2YEUZFPj6Q2bTWQ4oFdbor8lxYQtOLdsDS4aTLeKA5gVkn1qU/jFnXIedW7EGHcQg9c9VwBAzWW+wCPixPF4L+5N9qDXc3hRy/O4nLPWizDIMyLzVcyO7IefigNASaneFsCgbBwIDAQAB";
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCnJZvaC4GnKbcTZuQo/MzhaJnq5VZtebx95anso60VXdRL6gTNDwjqXnXgZxJXDZ7JUZ1BcXXoRwKTO3ZTtPCpxu2Sp7ivf+1+faUW3eCK4Jad1FIhT+/CBfNaW8sPD4E0uRv9cO2O2gC7iMT0935QV45HYHNj2/PqH9jy56nB/uunIkrRiJsDJObUqDhOE1yKO8kxLq9V6BhGO/ZgRRkU+PpDZtNZDigV1uivyXFhC04t2wNLhpMt4oDmBWSfWpT+MWdch51bsQYdxCD1z1XAEDNZb7AI+LE8Xgv7k32oNdzeFHL87ics9aLMMgzIvNVzI7sh5+KA0BJqd4WwKBsHAgMBAAECggEAOVarekdMON4UkbFQP8dJOWI6utvlpDSItt+cRTN44sWHWGt1LiskMdIpucth/T8M4579xT945S/G8F8LnneJv5QgV5j1wC6weB3+DjQbHUkiIg5+BjN32iVUcADhTc9R0ZZWxcCBnemnKq4mcqqcSE9g3Zd2viDsMbqvH9Tc5VFRrokmNquumZC9kyvKBfMnXlVsy/68QnhqoMQxHsWdvtti/DQV1y328PSzRPdR7MIQo0CdxTfcdYNSW7w1KY/b1t5GfSqnR60wKkyHgD/8AWBkaUdGV3J6ZN6nP1UOqknkyTxHiSVldHPHuSSqfFM+bDlejwDh3PSpXiKTjwAhIQKBgQDcLzu6YhQoop15QqdD/BboYsLPHeEYvoNC7jlyOCgvsKjFkVLg2SKTUoUg8cJxug3M9Vdbjg0MHY3ZCYh+A4rTx+RUBXwTeyPc0N0dBIQMk6s8AGISohMm321JQU7wURWseeX1B7AjuWrlHTXg2zk/0QQdbkoD+1qSqbNnnAF2+QKBgQDCVdJVIm9mOXTsN18QleJXCElGlEDmfabdZGmF97XU6a+4iaRscFheu6vqkTOrrzwGkajJoSAk6nuxARoHlnK1OY5xj70flpOtKpjhf+6dztuki9HK33l1PIV+rgMT2YWiSvtPgYp1TJWxcYl23XBVB7g8qIg/Pk7Hqds2reCh/wKBgApwRtGgQrpmczbDfTCQ6/kabYfqL7i/l3N93cVXejhAlM/BAv/b63K2j88vBvvjSTI1U23ovx+a6xWmCQ2IV3WQ3XdKR0aZKOsYD9OHWkdKXOzcS8n8WxIVt5WoffiQr4lcwhUqxyP6b2HrT5mYaw9mdl560xF7QiD/4JyB3ePpAoGBAIbUwI/sylmkd6kEaeK5+0XjK+PaLIU+nK1yY3xddzz3vcFNTlUmRUnE7+xZPf5aIE12diMetYynW5Zda0/kZEW0X04PKmpmMKOusE+UUVwQD3hEwhP9pOlgSkawONZmzz7gwyhQel4T+rAYVAWycaOtLXh4fb9TXZlOEhgH35jFAoGAeBSgav/fOCZYHVPk3H/c70DhJ0b4RM/fd5598z4oH/bYWogBuxzTbp6Rt+OyogYKps4XgdPP5zS5oWaGTwvP990qxEquqg2+zj/WQiRNJheWVYn8gpuea9hDDWpsidJUbp+MvZ+yxF8IMyRYPQ5+hHYQIRy0ho0RiMrCHDyVbyU=";
        String encryptBase64 = encryptByPublicKey("test1234", publicKey);
        System.out.println(encryptBase64);
        System.out.println(decryptByPrivateKey(encryptBase64, privateKey));
    }

}
