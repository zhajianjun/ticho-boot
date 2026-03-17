package top.ticho.tool.core;

import top.ticho.tool.core.constant.TiStrConst;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常处理工具类
 * <p>
 * 提供异常信息的转换和格式化功能，包括堆栈跟踪转字符串、获取异常消息等。
 * </p>
 *
 * @author zhajianjun
 * @date 2025-08-17 15:13
 */
public class TiExceptionUtil {

    /**
     * 将异常的堆栈跟踪转换为字符串
     * <p>
     * 默认最大长度为 3000 个字符，超出部分将被截断。
     * </p>
     *
     * @param throwable 要转换的异常对象
     * @return 异常堆栈跟踪的字符串表示，如果异常为 null 则返回空字符串
     * @see #stacktraceToString(Throwable, int)
     */
    public static String stacktraceToString(Throwable throwable) {
        return stacktraceToString(throwable, 3000);
    }

    /**
     * 将异常的堆栈跟踪转换为指定长度的字符串
     * <p>
     * 如果堆栈跟踪超过指定长度，超出部分将被截断。
     * </p>
     *
     * @param throwable 要转换的异常对象
     * @param length    返回字符串的最大长度
     * @return 异常堆栈跟踪的字符串表示，如果异常为 null 则返回空字符串
     */
    public static String stacktraceToString(Throwable throwable, int length) {
        if (throwable == null) {
            return TiStrConst.EMPTY;
        }
        final StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        String stackTrace = sw.toString();
        return TiStrUtil.substring(stackTrace, 0, length);
    }

    /**
     * 获取异常的简要消息信息
     * <p>
     * 返回格式为："异常类名：异常消息"，如果异常消息为空则返回空字符串。
     * </p>
     *
     * @param e 要获取消息的异常对象
     * @return 格式化的异常消息，如果异常为 null 则返回空字符串
     */
    public static String getMessage(Throwable e) {
        if (e == null) {
            return TiStrConst.EMPTY;
        }
        final String clsName = TiClassUtil.getShortClassName(e.getClass());
        return clsName + ": " + TiStrUtil.defaultIfEmpty(e.getMessage(), TiStrConst.EMPTY);
    }

}
