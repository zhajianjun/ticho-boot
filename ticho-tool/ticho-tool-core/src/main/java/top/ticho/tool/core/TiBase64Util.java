package top.ticho.tool.core;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    public static String encode(String source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        byte[] encode = Base64.getEncoder().encode(source.getBytes(StandardCharsets.UTF_8));
        return new String(encode, StandardCharsets.UTF_8);
    }

    public static String decode(String source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        byte[] decode = Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8));
        return new String(decode, StandardCharsets.UTF_8);
    }

}
