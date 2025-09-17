package top.ticho.tool.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
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

    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

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

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    /**
     * 重复指定字符指定次数，生成新的字符串
     *
     * @param c     要重复的字符
     * @param count 重复次数
     * @return 由指定字符重复指定次数组成的新字符串，如果count小于等于0则返回空字符串
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


    public static int indexOf(CharSequence str, char searchChar) {
        return indexOf(str, searchChar, 0);
    }

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

    public static String subBefore(String string, String separator) {
        return StringUtils.substringBefore(string, separator);
    }

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

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static String cleanBlank(String str) {
        return filter(str, TiStrUtil::isNotBlankChar);
    }

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

    public static String format(String template, Object... params) {
        return TiStrFormatter.format(template, params);
    }

    public static boolean startWithIgnoreCase(String str, String prefix) {
        return startWith(str, prefix, true, false);
    }

    public static String strip(String str, String prefixOrSuffix) {
        return strip(str, prefixOrSuffix, prefixOrSuffix);
    }

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

    public static String removeSuffix(String str, String suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return null;
        }
        if (str.endsWith(suffix)) {
            return substringPre(str, str.length() - suffix.length());// 截取前半段
        }
        return str;
    }

    public static String removePrefix(String str, String prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return null;
        }
        if (str.startsWith(prefix)) {
            return substringSuf(str, prefix.length());// 截取后半段
        }
        return str;
    }

    public static List<String> split(String str, String separator) {
        return Arrays.stream(str.split(separator))
            .collect(Collectors.toList());
    }

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

    public static boolean equals(String str1, String str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }
        if (ignoreCase) {
            return str1.equalsIgnoreCase(str2);
        } else {
            return str1.contentEquals(str2);
        }
    }

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

    public static String substringPre(String string, int toIndexExclude) {
        return substring(string, 0, toIndexExclude);
    }

    public static String substringSuf(String string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return substring(string, fromIndex, string.length());
    }

    public static String replace(String text, String searchString, String replacement, boolean ignoreCase) {
        Strings cs = ignoreCase ? Strings.CI : Strings.CS;
        return cs.replace(text, searchString, replacement);
    }

    public static String str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }

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

    public static boolean isNotBlankChar(int c) {
        return !isBlankChar(c);
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

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

}
