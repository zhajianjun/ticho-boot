package top.ticho.tool.core;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:41
 */
public class TiIdUtil {
    private static final Snowflake snowflake = IdUtil.getSnowflake(0, 0);
    private static final String[] CHARS = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static Long getId() {
        return snowflake.nextId();
    }

    public static String getIdStr() {
        return snowflake.nextIdStr();
    }

    public static String simpleUuid() {
        return IdUtil.fastSimpleUUID();
    }

    public static String uuid() {
        return IdUtil.fastUUID();
    }

    public static String shortUuid() {
        // 调用Java提供的生成随机字符串的对象：32位，十六进制，中间包含-
        StringBuilder builder = new StringBuilder();
        String uuidStr = simpleUuid();
        // 分为8组
        for (int i = 0; i < 8; i++) {
            // 每组4位
            String str = uuidStr.substring(i * 4, i * 4 + 4);
            // 将4位str转化为int 16进制下的表示
            int x = Integer.parseInt(str, 16);
            // 用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
            builder.append(CHARS[x % 0x3E]);
        }
        return builder.toString();
    }

}
