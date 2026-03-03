package top.ticho.tool.core;

import top.ticho.tool.core.constant.TiStrConst;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:32
 */
public class TiStrUtil {
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 如果字符串是空白，则返回默认值
     *
     * @param str        要检查的字符串
     * @param defaultStr 默认值
     * @return 如果字符串是空白则返回默认值，否则返回原字符串
     */
    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * 如果字符串为空，则返回默认值
     *
     * @param str        要检查的字符串
     * @param defaultStr 默认值
     * @return 如果字符串为空则返回默认值，否则返回原字符串
     */
    public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 如果字符串为 null，则返回默认值
     *
     * @param str        要检查的字符串
     * @param defaultStr 默认值
     * @return 如果字符串为 null 则返回默认值，否则返回原字符串
     */
    public static <T extends CharSequence> T defaultIfNull(final T str, final T defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 判断字符串是否为空白（只包含空白字符或为空）
     *
     * @param cs 要检查的字符串
     * @return 如果字符串为空白则返回 true，否则返回 false
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空白
     *
     * @param cs 要检查的字符串
     * @return 如果字符串不为空白则返回 true，否则返回 false
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 判断字符串是否为空（null 或长度为 0）
     *
     * @param cs 要检查的字符串
     * @return 如果字符串为空则返回 true，否则返回 false
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param cs 要检查的字符串
     * @return 如果字符串不为空则返回 true，否则返回 false
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断字符串是否只包含数字
     *
     * @param cs 要检查的字符串
     * @return 如果字符串只包含数字则返回 true，否则返回 false
     */
    public static boolean isNumber(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除字符串中的所有空白字符
     *
     * @param str 要处理的字符串
     * @return 删除空白后的字符串
     */
    public static String deleteWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        if (count == 0) {
            return TiStrConst.EMPTY;
        }
        return new String(chs, 0, count);
    }

    /**
     * 重复指定字符指定次数，生成新的字符串
     *
     * @param c     要重复的字符
     * @param count 重复次数
     * @return 由指定字符重复指定次数组成的新字符串，如果 count 小于等于 0 则返回空字符串
     */
    public static String repeat(char c, int count) {
        // 处理无效的重复次数
        if (count <= 0) {
            return TiStrConst.EMPTY;
        }
        // 创建字符数组并填充指定字符
        char[] result = new char[count];
        Arrays.fill(result, c);
        // 转换为字符串并返回
        return new String(result);
    }


    /**
     * 查找字符在字符串中首次出现的位置
     *
     * @param str        要搜索的字符串
     * @param searchChar 要查找的字符
     * @return 字符首次出现的索引位置，如果未找到返回 -1
     */
    public static int indexOf(CharSequence str, char searchChar) {
        return indexOf(str, searchChar, 0);
    }

    /**
     * 从指定位置开始查找字符在字符串中首次出现的位置
     *
     * @param cs         要搜索的字符串
     * @param searchChar 要查找的字符（支持 Unicode 补充字符）
     * @param start      搜索起始位置
     * @return 字符首次出现的索引位置，如果未找到返回 -1
     */
    public static int indexOf(final CharSequence cs, final int searchChar, int start) {
        if (isEmpty(cs)) {
            return Character.DIRECTIONALITY_UNDEFINED;
        }
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        if (searchChar < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            for (int i = start; i < sz; i++) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
            return Character.DIRECTIONALITY_UNDEFINED;
        }
        if (searchChar <= Character.MAX_CODE_POINT) {
            final char[] chars = Character.toChars(searchChar);
            for (int i = start; i < sz - 1; i++) {
                final char high = cs.charAt(i);
                final char low = cs.charAt(i + 1);
                if (high == chars[0] && low == chars[1]) {
                    return i;
                }
            }
        }
        return Character.DIRECTIONALITY_UNDEFINED;
    }

    /**
     * 获取指定分隔符之前的子字符串
     *
     * @param str       要处理的字符串
     * @param separator 分隔符
     * @return 分隔符之前的子字符串，如果未找到分隔符则返回原字符串
     */
    public static String subBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return TiStrConst.EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * 按指定长度分割字符串
     *
     * @param str    要分割的字符串
     * @param length 每段的长度
     * @return 分割后的字符串数组
     */
    public static String[] split(String str, int length) {
        if (str == null || str.isEmpty() || length <= 0) {
            return new String[0];
        }
        List<String> result = new ArrayList<>();
        int start = 0;
        while (start < str.length()) {
            int end = Math.min(start + length, str.length());
            result.add(str.substring(start, end));
            start += length;
        }
        return result.toArray(new String[0]);
    }

    /**
     * 按指定分隔符分割字符串
     *
     * @param str       要分割的字符串
     * @param separator 分隔符
     * @return 分割后的字符串列表
     */
    public static List<String> split(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(separator))
            .collect(Collectors.toList());
    }

    /**
     * 移除字符串前缀（忽略大小写）
     *
     * @param str    要处理的字符串
     * @param prefix 要移除的前缀
     * @return 移除前缀后的字符串
     */
    public static String removePrefixIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return str;
        }
        // 检查字符串是否以指定前缀开头（忽略大小写）
        if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
            // 移除前缀部分
            return str.substring(prefix.length());
        }
        return str;
    }

    /**
     * 去除字符串两端的空白字符
     *
     * @param str 要处理的字符串
     * @return 去除两端空白后的字符串
     */
    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    /**
     * 清理字符串中的空白字符
     *
     * @param str 要处理的字符串
     * @return 清理空白后的字符串
     */
    public static String cleanBlank(String str) {
        return filter(str, TiStrUtil::isNotBlankChar);
    }

    /**
     * 隐藏字符串中指定范围的字符（替换为*）
     *
     * @param str          要处理的字符串
     * @param startInclude 起始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @return 隐藏指定范围字符后的字符串
     */
    public static String hide(String str, int startInclude, int endExclude) {
        if (isEmpty(str)) {
            return null;
        }
        int[] strCodePoints = str.codePoints().toArray();
        final int strLength = strCodePoints.length;
        if (startInclude > strLength) {
            return str;
        }
        if (endExclude > strLength) {
            endExclude = strLength;
        }
        if (startInclude > endExclude) {
            // 如果起始位置大于结束位置，不替换
            return str;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strLength; i++) {
            if (i >= startInclude && i < endExclude) {
                stringBuilder.append("*");
            } else {
                stringBuilder.append(new String(strCodePoints, i, 1));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 格式化字符串，使用 {} 作为占位符
     *
     * @param template 模板字符串
     * @param params   参数列表
     * @return 格式化后的字符串
     */
    public static String format(String template, Object... params) {
        return TiStrFormatter.format(template, params);
    }

    /**
     * 判断字符串是否以指定前缀开头（忽略大小写）
     *
     * @param str    要检查的字符串
     * @param prefix 前缀
     * @return 如果以指定前缀开头则返回 true，否则返回 false
     */
    public static boolean startWithIgnoreCase(String str, String prefix) {
        return startWith(str, prefix, true, false);
    }

    /**
     * 去除字符串的前缀和后缀
     *
     * @param str            要处理的字符串
     * @param prefixOrSuffix 前缀或后缀
     * @return 去除前缀和后缀后的字符串
     */
    public static String strip(String str, String prefixOrSuffix) {
        return strip(str, prefixOrSuffix, prefixOrSuffix);
    }

    /**
     * 去除字符串的前缀和后缀
     *
     * @param str    要处理的字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 去除前缀和后缀后的字符串
     */
    public static String strip(String str, String prefix, String suffix) {
        if (isEmpty(str)) {
            return null;
        }
        int from = 0;
        int to = str.length();
        if (startWith(str, prefix, false, false)) {
            from = prefix.length();
        }
        if (endWith(str, suffix, false, false)) {
            to -= suffix.length();
        }
        return str.substring(Math.min(from, to), Math.max(from, to));
    }

    /**
     * 移除字符串后缀
     *
     * @param str    要处理的字符串
     * @param suffix 要移除的后缀
     * @return 移除后缀后的字符串
     */
    public static String removeSuffix(String str, String suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return null;
        }
        if (str.endsWith(suffix)) {
            return substringPre(str, str.length() - suffix.length());// 截取前半段
        }
        return str;
    }

    /**
     * 移除字符串前缀
     *
     * @param str    要处理的字符串
     * @param prefix 要移除的前缀
     * @return 移除前缀后的字符串
     */
    public static String removePrefix(String str, String prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return null;
        }
        if (str.startsWith(prefix)) {
            return substringSuf(str, prefix.length());// 截取后半段
        }
        return str;
    }

    /**
     * 根据条件过滤字符串中的字符
     *
     * @param str    要处理的字符串
     * @param filter 字符过滤条件
     * @return 过滤后的字符串
     */
    public static String filter(String str, final Predicate<Character> filter) {
        if (str == null || filter == null) {
            return null;
        }
        int len = str.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (filter.test(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否以指定前缀开头
     *
     * @param str          要检查的字符串
     * @param prefix       前缀
     * @param ignoreCase   是否忽略大小写
     * @param ignoreEquals 是否忽略相等的情况（即完全匹配时返回 false）
     * @return 如果以指定前缀开头则返回 true，否则返回 false
     */
    public static boolean startWith(String str, String prefix, boolean ignoreCase, boolean ignoreEquals) {
        if (null == str || null == prefix) {
            if (ignoreEquals) {
                return false;
            }
            return null == str && null == prefix;
        }
        boolean isStartWith = str
            .regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
        if (isStartWith) {
            return (!ignoreEquals) || (!equals(str, prefix, ignoreCase));
        }
        return false;
    }

    /**
     * 判断字符串是否以指定后缀结尾
     *
     * @param str          要检查的字符串
     * @param suffix       后缀
     * @param ignoreCase   是否忽略大小写
     * @param ignoreEquals 是否忽略相等的情况（即完全匹配时返回 false）
     * @return 如果以指定后缀结尾则返回 true，否则返回 false
     */
    public static boolean endWith(String str, String suffix, boolean ignoreCase, boolean ignoreEquals) {
        if (null == str || null == suffix) {
            if (ignoreEquals) {
                return false;
            }
            return null == str && null == suffix;
        }
        final int strOffset = str.length() - suffix.length();
        boolean isEndWith = str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
        if (isEndWith) {
            return (!ignoreEquals) || (!equals(str, suffix, ignoreCase));
        }
        return false;
    }

    /**
     * 比较两个字符串是否相等
     *
     * @param str1       字符串 1
     * @param str2       字符串 2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相等则返回 true，否则返回 false
     */
    public static boolean equals(String str1, String str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为 null 才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串 2 空，字符串 1 非空，直接 false
            return false;
        }
        if (ignoreCase) {
            return str1.equalsIgnoreCase(str2);
        } else {
            return str1.contentEquals(str2);
        }
    }

    /**
     * 截取字符串（从指定位置到末尾）
     *
     * @param str   要截取的字符串
     * @param start 起始位置（负数表示从末尾开始计算）
     * @return 截取后的子字符串
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return TiStrConst.EMPTY;
        }
        return str.substring(start);
    }

    /**
     * 截取字符串（指定起始和结束位置）
     *
     * @param str   要截取的字符串
     * @param start 起始位置（负数表示从末尾开始计算）
     * @param end   结束位置（负数表示从末尾开始计算）
     * @return 截取后的子字符串
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return TiStrConst.EMPTY;
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    /**
     * 截取字符串的前半部分（从开头到指定位置）
     *
     * @param string         要截取的字符串
     * @param toIndexExclude 结束位置（不包含）
     * @return 截取后的子字符串
     */
    public static String substringPre(String string, int toIndexExclude) {
        return substring(string, 0, toIndexExclude);
    }

    /**
     * 截取字符串的后半部分（从指定位置到末尾）
     *
     * @param string    要截取的字符串
     * @param fromIndex 起始位置
     * @return 截取后的子字符串
     */
    public static String substringSuf(String string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return substring(string, fromIndex, string.length());
    }

    /**
     * 替换字符串中的指定内容（可选择是否忽略大小写）
     *
     * @param text         原始文本
     * @param searchString 要查找并替换的内容
     * @param replacement  替换后的内容
     * @param ignoreCase   是否忽略大小写
     * @return 替换后的字符串
     */
    public static String replace(String text, String searchString, String replacement, boolean ignoreCase) {
        if (text == null || text.isEmpty() || searchString == null || searchString.isEmpty()) {
            return text;
        }
        if (replacement == null) {
            replacement = "";
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i <= text.length() - searchString.length()) {
            if (text.regionMatches(ignoreCase, i, searchString, 0, searchString.length())) {
                result.append(replacement);
                i += searchString.length();
            } else {
                result.append(text.charAt(i));
                i++;
            }
        }
        result.append(text.substring(i));
        return result.toString();
    }

    /**
     * 将对象转换为字符串（使用 UTF-8 编码）
     *
     * @param obj 要转换的对象
     * @return 转换后的字符串
     */
    public static String str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }

    /**
     * 将对象转换为字符串
     * 支持以下类型：String、byte[]、Byte[]、ByteBuffer、数组
     *
     * @param obj     要转换的对象
     * @param charset 字符编码
     * @return 转换后的字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[] bytes) {
            return new String(bytes, charset);
        } else if (obj instanceof Byte[] byteObjects) {
            byte[] bytes = new byte[byteObjects.length];
            for (int i = 0; i < byteObjects.length; i++) {
                bytes[i] = byteObjects[i];
            }
            return new String(bytes, charset);
        } else if (obj instanceof ByteBuffer byteBuffer) {
            byte[] bytes;
            if (byteBuffer.hasArray()) {
                bytes = byteBuffer.array();
            } else {
                bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
            }
            return new String(bytes, charset);
        } else if (TiArrayUtil.isArray(obj)) {
            return TiArrayUtil.toString(obj);
        }
        return obj.toString();
    }

    /**
     * 判断字符是否不为空白
     *
     * @param c 要判断的字符
     * @return 如果字符不为空白则返回 true，否则返回 false
     */
    public static boolean isNotBlankChar(int c) {
        return !isBlankChar(c);
    }

    /**
     * 获取字符串的长度（处理 null 的情况）
     *
     * @param cs 要获取长度的字符串
     * @return 字符串的长度，如果为 null 则返回 0
     */
    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 判断字符是否为空白字符
     * 包括常见的空白字符以及一些特殊的 Unicode 空白字符
     *
     * @param c 要判断的字符
     * @return 如果字符是空白字符则返回 true，否则返回 false
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c)
            || Character.isSpaceChar(c)
            || c == '\ufeff'
            || c == '\u202a'
            || c == '\u0000'
            // issue#I5UGSQ，Hangul Filler
            || c == '\u3164'
            // Braille Pattern Blank
            || c == '\u2800'
            // MONGOLIAN VOWEL SEPARATOR
            || c == '\u180e';
    }

    /**
     * 判断字符串是否为布尔值（"true" 或 "false"）
     *
     * @param cs 要判断的字符串
     * @return 如果是布尔值则返回 true，否则返回 false
     */
    public static boolean isBool(final CharSequence cs) {
        return "true".contentEquals(cs) || "false".contentEquals(cs);
    }

}
