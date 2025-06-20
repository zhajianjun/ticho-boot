package top.ticho.generator.function;

import top.ticho.generator.util.StrUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhajianjun
 * @date 2024-11-24 15:45
 */
public class StringUtil extends org.beetl.ext.fn.StringUtil {
    public final String HYPHEN = "-";
    public final String UNDERSCORE = "_";
    public final String UNDERLINE = "_";
    private final String[] chars = new String[]{
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
        "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
        "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    public String generateId() {
        String[] chars = getChars();
        char[] idChars = new char[8];
        String uuid = UUID.randomUUID().toString().replace(HYPHEN, "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            idChars[i] = chars[x % 0x3E].charAt(0);
        }
        return new String(idChars);
    }

    public String[] getChars() {
        return Arrays.copyOf(chars, chars.length);
    }

    /**
     * 首字符大写
     *
     * @param str String
     * @return String
     */
    public String toUpperFirst(String str) {
        if (str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 首字符小写
     *
     * @param str String
     * @return String
     */

    public String toLowerFirst(String str) {
        if (str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 转下划线
     */
    public String toUnderline(String str) {
        return splitToStream(str).collect(Collectors.joining(UNDERSCORE));
    }

    /**
     * 转横线
     */
    public String toHyphen(String str) {
        return splitToStream(str).collect(Collectors.joining(UNDERLINE));
    }

    /**
     * 转驼峰，首字母小写
     */
    public String toCamelUF(String str) {
        return splitToStream(str).map(StrUtil::toUpperFirst).collect(Collectors.joining());
    }

    /**
     * 转驼峰，首字母大写
     */
    public String toCamelLF(String str) {
        return toLowerFirst(toCamelUF(str));
    }

    /**
     * 分割字符串(包含驼峰、下划线、横杠)，返回Stream流。
     */
    private Stream<String> splitToStream(String str) {
        if (isBlank(str)) {
            return Stream.empty();
        }
        String[] split = str.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[a-z0-9])(?=[A-Z])|(?<=[0-9])(?=[a-zA-Z])|-|_");
        return Arrays.stream(split)
            .filter(StrUtil::isNotBlank)
            .map(String::toLowerCase);
    }

    public boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 字符串按照长度分割
     * 分割位置为逗号，如果找不到逗号，则分割位置为字符串长度
     *
     * @param str       str
     * @param split     分割字符串
     * @param maxLength 最大长度
     * @return {@link List }<{@link String }>
     */
    public List<String> splitLine(String str, String split, int maxLength) {
        List<String> result = new ArrayList<>();
        if (isEmpty(str)) {
            return result;
        }
        // 长度小于等于最大长度，直接返回
        if (str.length() <= maxLength) {
            result.add(str);
            return result;
        }
        int start = 0;
        while (start < str.length()) {
            int end = Math.min(start + maxLength, str.length());
            // 当前段内最后一个逗号
            int commaPos = str.lastIndexOf(split, end);
            // 当前段无逗号，向后找第一个逗号
            if (commaPos <= start) {
                commaPos = str.indexOf(split, end);
                // 无后续逗号则截取到末尾
                if (commaPos == -1) {
                    commaPos = str.length();
                }
            }
            // 异常保护
            if (commaPos == -1) {
                break;
            }
            result.add(str.substring(start, Math.min(commaPos + 1, str.length())));
            start = commaPos + 1;
        }
        return result;
    }

    public boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public boolean isEmpty(CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    public boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public String emptyDefault(String suffix, String defaultValue) {
        return isEmpty(suffix) ? defaultValue : suffix;
    }

    // 字符串中提取出数字
    public String extractNumber(String str) {
        return str.replaceAll("[^0-9]", "");
    }

}
