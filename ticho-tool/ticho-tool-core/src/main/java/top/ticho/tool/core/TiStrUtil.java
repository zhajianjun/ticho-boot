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

    public static <T extends String> T blankToDefault(T str, T defaultStr) {
        return StringUtils.defaultIfBlank(str, defaultStr);
    }

    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static String repeat(char c, int count) {
        if (count <= 0) {
            return TiStrConst.EMPTY;
        }
        char[] result = new char[count];
        Arrays.fill(result, c);
        return new String(result);
    }

    public static int indexOf(CharSequence str, char searchChar) {
        return StringUtils.indexOf(str, searchChar, 0);
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

    public static String trim(String str, int mode) {
        String result;
        if (str == null) {
            result = null;
        } else {
            int length = str.length();
            int start = 0;
            int end = length;// 扫描字符串头部
            if (mode <= 0) {
                while ((start < end) && (isBlankChar(str.charAt(start)))) {
                    start++;
                }
            }// 扫描字符串尾部
            if (mode >= 0) {
                while ((start < end) && (isBlankChar(str.charAt(end - 1)))) {
                    end--;
                }
            }
            if ((start > 0) || (end < length)) {
                result = str.substring(start, end);
            } else {
                result = str;
            }
        }
        return result;
    }

    public static String trimStart(String str) {
        return trim(str, -1);
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
            return subPre(str, str.length() - suffix.length());// 截取前半段
        }
        return str;
    }

    public static String removePrefix(String str, String prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return null;
        }
        if (str.startsWith(prefix)) {
            return subSuf(str, prefix.length());// 截取后半段
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

    public static String sub(String str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return null;
        }
        int len = str.length();
        if (fromIndexInclude < 0) {
            fromIndexInclude = len + fromIndexInclude;
            if (fromIndexInclude < 0) {
                fromIndexInclude = 0;
            }
        } else if (fromIndexInclude > len) {
            fromIndexInclude = len;
        }
        if (toIndexExclude < 0) {
            toIndexExclude = len + toIndexExclude;
            if (toIndexExclude < 0) {
                toIndexExclude = len;
            }
        } else if (toIndexExclude > len) {
            toIndexExclude = len;
        }
        if (toIndexExclude < fromIndexInclude) {
            int tmp = fromIndexInclude;
            fromIndexInclude = toIndexExclude;
            toIndexExclude = tmp;
        }
        if (fromIndexInclude == toIndexExclude) {
            return TiStrConst.EMPTY;
        }
        return str.substring(fromIndexInclude, toIndexExclude);
    }

    public static String subPre(String string, int toIndexExclude) {
        return sub(string, 0, toIndexExclude);
    }

    public static String subSuf(String string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }

    public static String replace(String str, String searchStr, String replacement) {
        return Strings.CS.replace(str, searchStr, replacement);
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
        } else if (obj instanceof byte[]) {
            return str(obj, charset);
        } else if (obj instanceof Byte[]) {
            return str(obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str(obj, charset);
        } else if (TiArrayUtil.isArray(obj)) {
            return TiArrayUtil.toString(obj);
        }
        return obj.toString();
    }

    public static boolean isNotBlankChar(int c) {
        return !isBlankChar(c);
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
