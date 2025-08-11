package top.ticho.tool.core;

import cn.hutool.core.util.StrUtil;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:32
 */
public class TiStrUtil {
    public static final String EMPTY = "";
    public static final String DOT = ".";

    public static boolean isBlankIfStr(Object obj) {
        return StrUtil.isBlankIfStr(obj);
    }

    public static boolean isBlank(CharSequence str) {
        return StrUtil.isBlank(str);
    }

    public static boolean isNotBlank(CharSequence str) {
        return StrUtil.isNotBlank(str);
    }

    public static boolean isEmpty(CharSequence str) {
        return StrUtil.isEmpty(str);
    }

    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        return StrUtil.subBefore(string, separator, isLastSeparator);
    }

    public static String[] split(CharSequence str, int len) {
        return StrUtil.split(str, len);
    }

    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        return StrUtil.removePrefixIgnoreCase(str, prefix);
    }

    public static String trim(CharSequence str, int mode) {
        return StrUtil.trim(str, -mode);
    }

    public static String trimStart(CharSequence str) {
        return StrUtil.trim(str, -1);
    }

    public static String hide(CharSequence str, int startInclude, int endExclude) {
        return StrUtil.hide(str, startInclude, endExclude);
    }

    public static String format(CharSequence template, Object... params) {
        return StrUtil.format(template, params);
    }

    public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return StrUtil.startWithIgnoreCase(str, prefix);
    }

}
