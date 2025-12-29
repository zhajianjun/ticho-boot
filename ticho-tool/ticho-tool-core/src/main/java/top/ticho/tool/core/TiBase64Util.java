package top.ticho.tool.core;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 11:59
 */
public class TiBase64Util {

    public static String encode(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        byte[] encode = Base64.getEncoder().encode(source);
        return new String(encode, StandardCharsets.UTF_8);
    }

    public static String encode(CharSequence source) {
        byte[] encodeAsBytes = encodeAsBytes(source);
        if (Objects.isNull(encodeAsBytes)) {
            return null;
        }
        return new String(encodeAsBytes, StandardCharsets.UTF_8);
    }

    public static String decode(CharSequence source) {
        byte[] decodeAsBytes = decodeAsBytes(source);
        if (Objects.isNull(decodeAsBytes)) {
            return null;
        }
        return new String(decodeAsBytes, StandardCharsets.UTF_8);
    }

    public static byte[] decode(byte[] source) {
        if (Objects.isNull(source)) {
            return null;
        }
        return Base64.getDecoder().decode(source);
    }

    public static byte[] encodeAsBytes(CharSequence source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return Base64.getEncoder().encode(source.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] decodeAsBytes(CharSequence source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return Base64.getDecoder().decode(source.toString().getBytes(StandardCharsets.UTF_8));
    }


}
