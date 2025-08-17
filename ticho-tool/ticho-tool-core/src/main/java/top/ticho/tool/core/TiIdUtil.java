package top.ticho.tool.core;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.github.f4b6a3.ulid.Ulid;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:41
 */
public class TiIdUtil {
    private static final Snowflake snowflake = IdUtil.getSnowflake(0, 0);
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static Long snowId() {
        return snowflake.nextId();
    }

    public static String snowIdStr() {
        return snowflake.nextIdStr();
    }

    public static String uuid() {
        return uuid(false);
    }

    public static String uuid(boolean isSimple) {
        return UUID.fastUUID().toString(isSimple);
    }

    public static String shortUuid() {
        // 调用Java提供的生成随机字符串的对象：32位，十六进制，中间包含-
        StringBuilder builder = new StringBuilder();
        String uuidStr = uuid().replace("-", "");
        // 分为8组
        for (int i = 0; i < 8; i++) {
            // 每组4位
            String str = uuidStr.substring(i * 4, i * 4 + 4);
            // 将4位str转化为int 16进制下的表示
            int x = Integer.parseInt(str, 16);
            // 用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
            builder.append(ALPHABET[x % 0x3E]);
        }
        return builder.toString();
    }

    public static String ulid() {
        return Ulid.fast().toString();
    }

    public static LocalDateTime timeFromUlid(String ulid) {
        Instant instant = Ulid.getInstant(ulid);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

}
