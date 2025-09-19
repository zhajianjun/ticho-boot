package top.ticho.tool.core;

import top.ticho.tool.core.constant.TiStrConst;

import java.io.PrintWriter;
import java.io.StringWriter;

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
        if (throwable == null) {
            return TiStrConst.EMPTY;
        }
        final StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        String stackTrace = sw.toString();
        return TiStrUtil.substring(stackTrace, 0, length);
    }

}
