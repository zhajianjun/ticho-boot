package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiUtilException;

/**
 * 十六进制工具类
 *
 * @author zhajianjun
 * @date 2026-02-04 20:33
 */
public class TiHexUtil {
    private static final char[] HEX_CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * 将十六进制字符串解码为字节数组
     *
     * @param source 十六进制字符串
     * @return 字节数组
     */
    public static byte[] decodeAsBytes(CharSequence source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        // 移除所有空白字符
        String hex = TiStrUtil.cleanBlank(source.toString()).toLowerCase();
        // 检查长度是否为偶数
        if (hex.length() % 2 != 0) {
            throw new TiUtilException("十六进制字符串长度必须为偶数");
        }
        // 检查是否包含有效的十六进制字符
        if (!hex.matches("[0-9A-F]+")) {
            throw new TiUtilException("字符串包含非法的十六进制字符");
        }
        int length = hex.length();
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);
            result[i / 2] = (byte) ((high << 4) | low);
        }
        return result;
    }

    /**
     * 将十六进制字符串解码为字符串
     *
     * @param hex 十六进制字符串
     * @return 解码后的字符串
     */
    public static String decodeAsString(CharSequence hex) {
        byte[] bytes = decodeAsBytes(hex);
        return bytes == null ? null : new String(bytes);
    }

    /**
     * 将字节数组编码为十六进制字符串
     *
     * @param source 字节数组
     * @return 十六进制字符串
     */
    public static String encode(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(source.length * 2);
        for (byte b : source) {
            // 使用位运算提取高4位和低4位
            sb.append(HEX_CHARS[(b >> 4) & 0x0F]);
            sb.append(HEX_CHARS[b & 0x0F]);
        }
        return sb.toString();
    }

    /**
     * 将字符串编码为十六进制字符串
     *
     * @param source 字符串
     * @return 十六进制字符串
     */
    public static String encodeString(String source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return encode(source.getBytes());
    }

}
