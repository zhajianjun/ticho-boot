package top.ticho.tool.core;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.TemporalAccessorUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-09 19:59
 */
public class TiLocalDateTimeUtil {
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";

    public static LocalDateTime of(long epochMilli) {
        return LocalDateTimeUtil.of(epochMilli);
    }

    public static LocalDateTime of(Instant instant) {
        return LocalDateTimeUtil.of(instant, ZoneId.systemDefault());
    }

    public static String formatNormal(LocalDateTime time) {
        return LocalDateTimeUtil.formatNormal(time);
    }

    public static String formatNormal(LocalDate time) {
        return LocalDateTimeUtil.formatNormal(time);
    }

    public static String formatNormal(LocalTime time) {
        return TemporalAccessorUtil.format(time, NORM_TIME_PATTERN);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return new Date(instant.toEpochMilli());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
