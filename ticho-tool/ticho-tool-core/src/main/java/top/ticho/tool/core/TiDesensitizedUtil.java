package top.ticho.tool.core;

import top.ticho.tool.core.constant.TiCharConst;
import top.ticho.tool.core.enums.TiDesensitizedType;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 12:16
 */
public class TiDesensitizedUtil {
    public static String desensitized(String str, TiDesensitizedType desensitizedType) {
        if (TiStrUtil.isBlank(str)) {
            return TiStrUtil.EMPTY;
        }
        String newStr = (str);
        switch (desensitizedType) {
            case USER_ID:
                newStr = String.valueOf(userId());
                break;
            case CHINESE_NAME:
                newStr = chineseName(str);
                break;
            case ID_CARD:
                newStr = idCardNum((str), 1, 2);
                break;
            case FIXED_PHONE:
                newStr = fixedPhone((str));
                break;
            case MOBILE_PHONE:
                newStr = mobilePhone((str));
                break;
            case ADDRESS:
                newStr = address((str), 8);
                break;
            case EMAIL:
                newStr = email((str));
                break;
            case PASSWORD:
                newStr = password((str));
                break;
            case CAR_LICENSE:
                newStr = carLicense((str));
                break;
            case BANK_CARD:
                newStr = bankCard((str));
                break;
            case IPV4:
                newStr = ipv4((str));
                break;
            case IPV6:
                newStr = ipv6((str));
                break;
            case FIRST_MASK:
                newStr = firstMask((str));
                break;
            case CLEAR_TO_EMPTY:
                newStr = clear();
                break;
            case CLEAR_TO_NULL:
                newStr = clearToNull();
                break;
            default:
        }
        return newStr;
    }

    /**
     * 清空为空字符串
     *
     * @return 清空后的值
     * @since 5.8.22
     */
    public static String clear() {
        return TiStrUtil.EMPTY;
    }

    /**
     * 清空为{@code null}
     *
     * @return 清空后的值(null)
     * @since 5.8.22
     */
    public static String clearToNull() {
        return null;
    }

    /**
     * 【用户id】不对外提供userId
     *
     * @return 脱敏后的主键
     */
    public static Long userId() {
        return 0L;
    }

    /**
     * 定义了一个first_mask的规则，只显示第一个字符。<br>
     * 脱敏前：123456789；脱敏后：1********。
     *
     * @param str 字符串
     * @return 脱敏后的字符串
     */
    public static String firstMask(String str) {
        if (TiStrUtil.isBlank(str)) {
            return TiStrUtil.EMPTY;
        }
        return TiStrUtil.hide(str, 1, str.length());
    }

    /**
     * 【中文姓名】只显示第一个汉字，其他隐藏为2个星号，比如：李**
     *
     * @param fullName 姓名
     * @return 脱敏后的姓名
     */
    public static String chineseName(String fullName) {
        return firstMask(fullName);
    }

    /**
     * 【身份证号】前1位 和后2位
     *
     * @param idCardNum 身份证
     * @param front     保留：前面的front位数；从1开始
     * @param end       保留：后面的end位数；从1开始
     * @return 脱敏后的身份证
     */
    public static String idCardNum(String idCardNum, int front, int end) {
        // 身份证不能为空
        if (TiStrUtil.isBlank(idCardNum)) {
            return TiStrUtil.EMPTY;
        }
        // 需要截取的长度不能大于身份证号长度
        if ((front + end) > idCardNum.length()) {
            return TiStrUtil.EMPTY;
        }
        // 需要截取的不能小于0
        if (front < 0 || end < 0) {
            return TiStrUtil.EMPTY;
        }
        return TiStrUtil.hide(idCardNum, front, idCardNum.length() - end);
    }

    /**
     * 【固定电话】 前四位，后两位
     *
     * @param num 固定电话
     * @return 脱敏后的固定电话；
     */
    public static String fixedPhone(String num) {
        if (TiStrUtil.isBlank(num)) {
            return TiStrUtil.EMPTY;
        }
        return TiStrUtil.hide(num, 4, num.length() - 2);
    }

    /**
     * 【手机号码】前三位，后4位，其他隐藏，比如135****2210
     *
     * @param num 移动电话；
     * @return 脱敏后的移动电话；
     */
    public static String mobilePhone(String num) {
        if (TiStrUtil.isBlank(num)) {
            return TiStrUtil.EMPTY;
        }
        return TiStrUtil.hide(num, 3, num.length() - 4);
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address       家庭住址
     * @param sensitiveSize 敏感信息长度
     * @return 脱敏后的家庭地址
     */
    public static String address(String address, int sensitiveSize) {
        if (TiStrUtil.isBlank(address)) {
            return TiStrUtil.EMPTY;
        }
        int length = address.length();
        return TiStrUtil.hide(address, length - sensitiveSize, length);
    }

    /**
     * 【电子邮箱】邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String email(String email) {
        if (TiStrUtil.isBlank(email)) {
            return TiStrUtil.EMPTY;
        }
        int index = TiStrUtil.indexOf(email, '@');
        if (index <= 1) {
            return email;
        }
        return TiStrUtil.hide(email, 1, index);
    }

    /**
     * 【密码】密码的全部字符都用*代替，比如：******
     *
     * @param password 密码
     * @return 脱敏后的密码
     */
    public static String password(String password) {
        if (TiStrUtil.isBlank(password)) {
            return TiStrUtil.EMPTY;
        }
        return TiStrUtil.repeat('*', password.length());
    }

    /**
     * 【中国车牌】车牌中间用*代替
     * eg1：null       -》 ""
     * eg1：""         -》 ""
     * eg3：苏D40000   -》 苏D4***0
     * eg4：陕A12345D  -》 陕A1****D
     * eg5：京A123     -》 京A123     如果是错误的车牌，不处理
     *
     * @param carLicense 完整的车牌号
     * @return 脱敏后的车牌
     */
    public static String carLicense(String carLicense) {
        if (TiStrUtil.isBlank(carLicense)) {
            return TiStrUtil.EMPTY;
        }
        // 普通车牌
        if (carLicense.length() == 7) {
            carLicense = TiStrUtil.hide(carLicense, 3, 6);
        } else if (carLicense.length() == 8) {
            // 新能源车牌
            carLicense = TiStrUtil.hide(carLicense, 3, 7);
        }
        return carLicense;
    }

    /**
     * 【银行卡号脱敏】由于银行卡号长度不定，所以只展示前4位，后面的位数根据卡号决定展示1-4位
     * 例如：
     * <pre>{@code
     *      1. "1234 2222 3333 4444 6789 9"    ->   "1234 **** **** **** **** 9"
     *      2. "1234 2222 3333 4444 6789 91"   ->   "1234 **** **** **** **** 91"
     *      3. "1234 2222 3333 4444 678"       ->   "1234 **** **** **** 678"
     *      4. "1234 2222 3333 4444 6789"      ->   "1234 **** **** **** 6789"
     *  }</pre>
     *
     * @param bankCardNo 银行卡号
     * @return 脱敏之后的银行卡号
     */
    public static String bankCard(String bankCardNo) {
        if (TiStrUtil.isBlank(bankCardNo)) {
            return bankCardNo;
        }
        bankCardNo = TiStrUtil.cleanBlank(bankCardNo);
        if (bankCardNo.length() < 9) {
            return bankCardNo;
        }

        final int length = bankCardNo.length();
        final int endLength = length % 4 == 0 ? 4 : length % 4;
        final int midLength = length - 4 - endLength;

        final StringBuilder buf = new StringBuilder();
        buf.append(bankCardNo, 0, 4);
        for (int i = 0; i < midLength; ++i) {
            if (i % 4 == 0) {
                buf.append(TiCharConst.SPACE);
            }
            buf.append('*');
        }
        buf.append(TiCharConst.SPACE).append(bankCardNo, length - endLength, length);
        return buf.toString();
    }

    /**
     * IPv4脱敏，如：脱敏前：192.0.2.1；脱敏后：192.*.*.*。
     *
     * @param ipv4 IPv4地址
     * @return 脱敏后的地址
     */
    public static String ipv4(String ipv4) {
        return TiStrUtil.subBefore(ipv4, ".") + ".*.*.*";
    }

    /**
     * IPv4脱敏，如：脱敏前：2001:0db8:86a3:08d3:1319:8a2e:0370:7344；脱敏后：2001:*:*:*:*:*:*:*
     *
     * @param ipv6 IPv4地址
     * @return 脱敏后的地址
     */
    public static String ipv6(String ipv6) {
        return TiStrUtil.subBefore(ipv6, ":") + ":*:*:*:*:*:*:*";
    }

}
