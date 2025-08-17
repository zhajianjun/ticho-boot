package top.ticho.tool.core;

import cn.hutool.core.exceptions.ExceptionUtil;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-17 15:13
 */
public class TiExceptionUtil {

    public static String stacktraceToString(Throwable throwable) {
        return ExceptionUtil.stacktraceToString(throwable);
    }

    public static String stacktraceToString(Throwable throwable, int limit) {
        return ExceptionUtil.stacktraceToString(throwable, limit, null);
    }

}
