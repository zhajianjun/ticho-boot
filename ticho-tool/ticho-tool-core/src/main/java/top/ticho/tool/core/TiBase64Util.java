package top.ticho.tool.core;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Base64 编码解码工具类
 * <p>提供 Base64 编码和解码的基本功能，支持字节数组和字符串的处理</p>
 *
 * @author zhajianjun
 * @date 2025-08-10 11:59
 */
public class TiBase64Util {

    /**
     * 将字节数组进行 Base64 编码
     *
     * @param source 要编码的字节数组
     * @return 编码后的字符串，如果源数组为 null 或长度为 0 则返回 null
     */
    public static String encode(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        byte[] encode = Base64.getEncoder().encode(source);
        return new String(encode, StandardCharsets.UTF_8);
    }

    /**
     * 将字符串进行 Base64 编码
     *
     * @param source 要编码的字符串
     * @return 编码后的字符串，如果源字符串为 null 或空则返回 null
     */
    public static String encode(CharSequence source) {
        byte[] encodeAsBytes = encodeAsBytes(source);
        if (Objects.isNull(encodeAsBytes)) {
            return null;
        }
        return new String(encodeAsBytes, StandardCharsets.UTF_8);
    }

    /**
     * 将 Base64 编码的字符串解码为普通字符串
     *
     * @param source 要解码的 Base64 字符串
     * @return 解码后的字符串，如果源字符串为 null 或空则返回 null
     */
    public static String decode(CharSequence source) {
        byte[] decodeAsBytes = decodeAsBytes(source);
        if (Objects.isNull(decodeAsBytes)) {
            return null;
        }
        return new String(decodeAsBytes, StandardCharsets.UTF_8);
    }

    /**
     * 将 Base64 编码的字节数组解码为原始字节数组
     *
     * @param source 要解码的字节数组
     * @return 解码后的字节数组，如果源数组为 null 则返回 null
     */
    public static byte[] decode(byte[] source) {
        if (Objects.isNull(source)) {
            return null;
        }
        return Base64.getDecoder().decode(source);
    }

    /**
     * 将字符串进行 Base64 编码并返回字节数组
     *
     * @param source 要编码的字符串
     * @return 编码后的字节数组，如果源字符串为 null 或空则返回 null
     */
    public static byte[] encodeAsBytes(CharSequence source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return Base64.getEncoder().encode(source.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将 Base64 编码的字符串解码为字节数组
     *
     * @param source 要解码的 Base64 字符串
     * @return 解码后的字节数组，如果源字符串为 null 或空则返回 null
     */
    public static byte[] decodeAsBytes(CharSequence source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return Base64.getDecoder().decode(source.toString().getBytes(StandardCharsets.UTF_8));
    }


}
