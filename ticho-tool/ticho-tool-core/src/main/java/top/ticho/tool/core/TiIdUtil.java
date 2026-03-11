package top.ticho.tool.core;


import top.ticho.tool.core.ulid.Ulid;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * 分布式 ID 生成工具类
 * 提供多种 ID 生成方式：雪花算法、UUID、短 UUID、ULID 等
 *
 * @author zhajianjun
 * @date 2025-08-04 22:41
 */
public class TiIdUtil {
    /**
     * Base62 编码字符表（大小写字母 + 数字）
     * 用于将 UUID 转换为更短的字符串格式
     */
    public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * 雪花算法实例（workerId=0, datacenterId=0）
     * 用于生成分布式唯一的长整型 ID
     */
    private static final TiSnowflake snowflake = new TiSnowflake(0, 0);


    /**
     * 生成雪花算法 ID（长整型）
     *
     * @return Long 类型的分布式唯一 ID
     */
    public static Long snowId() {
        return snowflake.nextId();
    }

    /**
     * 生成雪花算法 ID（字符串形式）
     *
     * @return String 类型的分布式唯一 ID
     */
    public static String snowIdStr() {
        return snowflake.nextIdStr();
    }

    /**
     * 生成标准 UUID（带连字符，32 位十六进制数，格式：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx）
     *
     * @return 标准格式的 UUID 字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成简化 UUID（去除连字符，32 位十六进制数）
     *
     * @return 无连字符的 UUID 字符串
     */
    public static String simpleUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成短 UUID（8 位 Base62 编码字符串）
     * 原理：将标准 UUID 分为 8 组，每组 4 位十六进制数转换为 1 位 Base62 字符
     *
     * @return 8 位短 UUID 字符串
     */
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

    /**
     * 生成 ULID（Universally Unique Lexicographically Sortable Identifier）
     * ULID 是一种字典排序的唯一标识符，具有时间有序性，长度为 26 个字符
     *
     * @return 26 位 ULID 字符串
     */
    public static String ulid() {
        return Ulid.fast().toString();
    }

    /**
     * 从 ULID 中提取时间信息
     * ULID 的前 48 位表示时间戳（毫秒级），可以还原为 LocalDateTime
     *
     * @param ulid ULID 字符串
     * @return UTC 时区的 LocalDateTime 对象
     */
    public static LocalDateTime timeFromUlid(String ulid) {
        Instant instant = Ulid.getInstant(ulid);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

}
