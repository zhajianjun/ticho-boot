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
 *
 *
 * @author zhajianjun
 * @date 2025-08-09 19:59
 */
public class TiLocalDateTimeUtil {

    public static LocalDateTime of(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return of(instant);
    }

    public static LocalDateTime of(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static String formatNormal(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS);
        return formatter.format(time);
    }

    public static String formatNormal(LocalDate time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD);
        return formatter.format(time);
    }

    public static String formatNormal(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TiDateFormatConst.HH_MM_SS);
        return formatter.format(time);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return new Date(instant.toEpochMilli());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
