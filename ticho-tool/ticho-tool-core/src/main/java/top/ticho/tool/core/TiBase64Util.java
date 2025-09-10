package top.ticho.tool.core;


import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

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
        return Base64.encodeBase64String(source);
    }

    public static String encode(String source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return Base64.encodeBase64String(source.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return new String(Base64.decodeBase64(source), StandardCharsets.UTF_8);
    }

}
