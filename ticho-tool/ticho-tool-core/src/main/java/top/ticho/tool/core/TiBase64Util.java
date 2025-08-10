package top.ticho.tool.core;

import cn.hutool.core.codec.Base64;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 11:59
 */
public class TiBase64Util {

    public static String encode(byte[] source) {
        return Base64.encode(source);
    }

}
