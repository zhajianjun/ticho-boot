package top.ticho.tool.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-17 15:13
 */
public class TiExceptionUtil {

    public static String stacktraceToString(Throwable throwable) {
        return stacktraceToString(throwable, 3000);
    }

    public static String stacktraceToString(Throwable throwable, int length) {
        String stackTrace = ExceptionUtils.getStackTrace(throwable);
        return StringUtils.substring(stackTrace, 0, length);
    }

}
