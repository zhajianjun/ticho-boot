
package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiUtilException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * 十六进制工具类
 * <p>提供十六进制字符串与字节数组之间的相互转换功能</p>
 *
 * @author zhajianjun
 * @date 2026-02-04 20:33
 */
public class TiHexUtil {

    /** 十六进制字符数组 */
    private static final char[] HEX_CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /** 十六进制字符映射表，用于快速查找字符对应的数值 */
    private static final int[] HEX_VALUES = new int[128];

    static {
        // 初始化十六进制字符映射表
        Arrays.fill(HEX_VALUES, -1);
        for (int i = 0; i < 10; i++) {
            HEX_VALUES['0' + i] = i;
        }
        for (int i = 0; i < 6; i++) {
            HEX_VALUES['A' + i] = 10 + i;
            HEX_VALUES['a' + i] = 10 + i;
        }
    }

    /**
     * 将十六进制字符串解码为字节数组
     *
     * @param source 十六进制字符串
     * @return 字节数组，如果输入为空则返回null
     * @throws TiUtilException 当输入格式不正确时抛出异常
     */
    public static byte[] decodeAsBytes(CharSequence source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        // 移除所有空白字符并转换为大写
        String hex = TiStrUtil.cleanBlank(source.toString()).toUpperCase();
        // 检查长度是否为偶数
        if (hex.length() % 2 != 0) {
            throw new TiUtilException("十六进制字符串长度必须为偶数，当前长度：" + hex.length());
        }
        // 验证字符有效性并转换
        int length = hex.length();
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            char highChar = hex.charAt(i);
            char lowChar = hex.charAt(i + 1);
            // 快速验证字符有效性
            if (highChar >= HEX_VALUES.length || lowChar >= HEX_VALUES.length ||
                HEX_VALUES[highChar] == -1 || HEX_VALUES[lowChar] == -1) {
                throw new TiUtilException("字符串包含非法的十六进制字符: " + highChar + lowChar);
            }
            int high = HEX_VALUES[highChar];
            int low = HEX_VALUES[lowChar];
            result[i / 2] = (byte) ((high << 4) | low);
        }
        return result;
    }

    /**
     * 将十六进制字符串解码为字符串
     *
     * @param hex 十六进制字符串
     * @return 解码后的字符串，如果输入为空则返回null
     * @throws TiUtilException 当输入格式不正确时抛出异常
     */
    public static String decodeAsString(CharSequence hex) {
        byte[] bytes = decodeAsBytes(hex);
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 将十六进制字符串解码为指定编码的字符串
     *
     * @param hex     十六进制字符串
     * @param charset 字符编码
     * @return 解码后的字符串，如果输入为空则返回null
     * @throws TiUtilException 当输入格式不正确时抛出异常
     */
    public static String decodeAsString(CharSequence hex, Charset charset) {
        byte[] bytes = decodeAsBytes(hex);
        return bytes == null ? null : new String(bytes, Objects.requireNonNull(charset, "charset不能为空"));
    }

    /**
     * 将字节数组编码为十六进制字符串
     *
     * @param source 字节数组
     * @return 十六进制字符串，如果输入为空则返回null
     */
    public static String encode(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        char[] result = new char[source.length * 2];
        for (int i = 0; i < source.length; i++) {
            byte b = source[i];
            result[i * 2] = HEX_CHARS[(b >> 4) & 0x0F];
            result[i * 2 + 1] = HEX_CHARS[b & 0x0F];
        }
        return new String(result);
    }

    /**
     * 将字符串编码为十六进制字符串（使用UTF-8编码）
     *
     * @param source 字符串
     * @return 十六进制字符串，如果输入为空则返回null
     */
    public static String encodeString(String source) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return encode(source.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将字符串按指定编码转换为十六进制字符串
     *
     * @param source  字符串
     * @param charset 字符编码
     * @return 十六进制字符串，如果输入为空则返回null
     */
    public static String encodeString(String source, Charset charset) {
        if (TiStrUtil.isEmpty(source)) {
            return null;
        }
        return encode(source.getBytes(Objects.requireNonNull(charset, "charset不能为空")));
    }

    /**
     * 验证字符串是否为有效的十六进制字符串
     *
     * @param hex 待验证的字符串
     * @return true表示是有效的十六进制字符串，false表示无效
     */
    public static boolean isValidHex(CharSequence hex) {
        if (TiStrUtil.isEmpty(hex)) {
            return false;
        }
        String cleanHex = TiStrUtil.cleanBlank(hex.toString());
        if (cleanHex.length() % 2 != 0) {
            return false;
        }
        for (int i = 0; i < cleanHex.length(); i++) {
            char c = cleanHex.charAt(i);
            if (c >= HEX_VALUES.length || HEX_VALUES[c] == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取十六进制字符串的字节长度
     *
     * @param hex 十六进制字符串
     * @return 字节长度，如果输入无效则返回-1
     */
    public static int getByteLength(CharSequence hex) {
        if (!isValidHex(hex)) {
            return -1;
        }
        return TiStrUtil.cleanBlank(hex.toString()).length() / 2;
    }

    public static void main(String[] args) {
        // 测试基本功能
        String original = "123456@qq.com";
        String encoded = encodeString(original);
        String decoded = decodeAsString(encoded);
        System.out.println("原始字符串: " + original);
        System.out.println("编码结果: " + encoded);
        System.out.println("解码结果: " + decoded);
        System.out.println("验证结果: " + original.equals(decoded));
        // 测试边界情况
        System.out.println("\n=== 边界测试 ===");
        System.out.println("空字符串编码: " + encodeString(""));
        System.out.println("null字符串编码: " + encodeString(null));
        System.out.println("null字节数组编码: " + encode(null));
        // 测试验证功能
        System.out.println("\n=== 验证测试 ===");
        System.out.println("有效十六进制: " + isValidHex("313031393331393437334071712E636F6D"));
        System.out.println("无效十六进制(奇数长度): " + isValidHex("313"));
        System.out.println("无效十六进制(非法字符): " + isValidHex("313G"));
        // 测试不同编码
        System.out.println("\n=== 编码测试 ===");
        String chinese = "你好世界";
        String gbkEncoded = encodeString(chinese, Charset.forName("GBK"));
        String gbkDecoded = decodeAsString(gbkEncoded, Charset.forName("GBK"));
        System.out.println("中文原字符串: " + chinese);
        System.out.println("GBK编码: " + gbkEncoded);
        System.out.println("GBK解码: " + gbkDecoded);
    }

}