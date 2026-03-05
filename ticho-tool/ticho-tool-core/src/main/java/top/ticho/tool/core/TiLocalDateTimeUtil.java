package top.ticho.tool.core;

import top.ticho.tool.core.constant.TiDateFormatConst;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 本地日期时间工具类
 * <p>提供时间戳、Instant、LocalDateTime、Date 之间的转换以及格式化功能</p>
 *
 * @author zhajianjun
 * @date 2025-08-09 19:59
 */
public class TiLocalDateTimeUtil {

    /**
     * 将时间戳转换为 LocalDateTime
     *
     * @param epochMilli 时间戳（毫秒）
     * @return LocalDateTime 对象
     */
    public static LocalDateTime of(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return of(instant);
    }

    /**
     * 将 Instant 转换为 LocalDateTime
     *
     * @param instant Instant 对象
     * @return LocalDateTime 对象
     */
    public static LocalDateTime of(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 格式化 LocalDateTime 为标准格式（yyyy-MM-dd HH:mm:ss）
     *
     * @param time 要格式化的 LocalDateTime
     * @return 格式化后的字符串
     */
    public static String formatNormal(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS);
        return formatter.format(time);
    }

    /**
     * 格式化 LocalDate 为标准格式（yyyy-MM-dd）
     *
     * @param time 要格式化的 LocalDate
     * @return 格式化后的字符串
     */
    public static String formatNormal(LocalDate time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD);
        return formatter.format(time);
    }

    /**
     * 格式化 LocalTime 为标准格式（HH:mm:ss）
     *
     * @param time 要格式化的 LocalTime
     * @return 格式化后的字符串
     */
    public static String formatNormal(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TiDateFormatConst.HH_MM_SS);
        return formatter.format(time);
    }

    /**
     * 将 LocalDateTime 转换为 Date
     *
     * @param localDateTime 要转换的 LocalDateTime
     * @return Date 对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return new Date(instant.toEpochMilli());
    }

    /**
     * 将 Date 转换为 LocalDateTime
     *
     * @param date 要转换的 Date
     * @return LocalDateTime 对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
