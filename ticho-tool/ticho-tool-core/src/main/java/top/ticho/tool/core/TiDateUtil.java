package top.ticho.tool.core;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-17 14:30
 */
public class TiDateUtil {

    public static String format(Date date, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(date.toInstant());
    }

}
