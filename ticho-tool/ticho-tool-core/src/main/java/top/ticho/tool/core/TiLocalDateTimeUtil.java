package top.ticho.tool.core;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.TemporalAccessorUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-09 19:59
 */
public class TiLocalDateTimeUtil {
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";

    public static String formatNormal(LocalDateTime time) {
        return LocalDateTimeUtil.formatNormal(time);
    }

    public static String formatNormal(LocalDate time) {
        return LocalDateTimeUtil.formatNormal(time);
    }

    public static String formatNormal(LocalTime time) {
        return TemporalAccessorUtil.format(time, NORM_TIME_PATTERN);
    }

}
