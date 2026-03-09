package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiUtilException;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 数字工具类
 * <p>提供数字类型转换、数值解析等常用操作</p>
 *
 * @author zhajianjun
 * @date 2025-08-08 23:28
 */
public class TiNumberUtil {

    /**
     * 将 Number 类型转换为 BigDecimal
     * <p>如果输入为 null，返回 BigDecimal.ZERO</p>
     * <p>支持 BigDecimal、Long、Integer、BigInteger、Float、Double 等类型</p>
     * <p>Float 和 Double 由于精度问题，会先转换为字符串再转换为 BigDecimal</p>
     * <p>对于无效的 Number（如 Infinity 或 NaN），会抛出异常</p>
     *
     * @param number 要转换的 Number 对象
     * @return 转换后的 BigDecimal，如果输入无效则抛出 TiUtilException
     * @throws TiUtilException 当数字为 Infinity 或 NaN 时抛出
     */
    public static BigDecimal toBigDecimal(Number number) {
        if (null == number) {
            return BigDecimal.ZERO;
        }
        // issue#3423@Github of CVE-2023-51080
        if (isValidNumber(number)) {
            throw new TiUtilException("Number is invalid!");
        }
        return switch (number) {
            case BigDecimal bigDecimal -> bigDecimal;
            case Long l -> new BigDecimal(l);
            case Integer integer -> new BigDecimal(integer);
            case BigInteger bigInteger -> new BigDecimal(bigInteger);
            // Float、Double 等有精度问题，转换为字符串后再转换
            default -> new BigDecimal(number.toString());
        };

    }

    /**
     * 将字符串转换为 BigDecimal
     * <p>如果字符串为空或空白，返回 BigDecimal.ZERO</p>
     * <p>如果字符串不是有效的数字格式，返回 null</p>
     *
     * @param numberStr 要转换的数字字符串
     * @return 转换后的 BigDecimal，如果字符串为空则返回 ZERO，如果格式无效则返回 null
     */
    public static BigDecimal toBigDecimal(String numberStr) {
        if (TiStrUtil.isBlank(numberStr)) {
            return BigDecimal.ZERO;
        }
        if (!TiStrUtil.isNumber(numberStr)) {
            return null;
        }
        return new BigDecimal(numberStr);
    }

    /**
     * 判断字符序列是否为数字
     * <p>委托给 TiStrUtil.isNumber 方法实现</p>
     *
     * @param str 要检查的字符序列
     * @return 如果是数字返回 true，否则返回 false
     */
    public static boolean isNumber(CharSequence str) {
        return TiStrUtil.isNumber(str);
    }

    /**
     * 将字符串解析为整数
     * <p>如果字符串不是有效的数字格式，返回 0</p>
     *
     * @param number 要解析的数字字符串
     * @return 解析后的整数值，如果格式无效则返回 0
     */
    public static int parseInt(String number) {
        if (!isNumber(number)) {
            return 0;
        }
        return Integer.parseInt(number);
    }


    /**
     * 验证 Number 是否有效
     * <p>检查 Double 和 Float 类型是否为有限数（非 Infinity 且非 NaN）</p>
     * <p>其他 Number 类型（如 Integer、Long、BigDecimal 等）默认都是有效的</p>
     *
     * @param number 要验证的 Number 对象
     * @return 如果数字有效返回 true，否则返回 false
     */
    public static boolean isValidNumber(Number number) {
        return switch (number) {
            case null -> false;
            case Double v -> (!v.isInfinite()) && (!v.isNaN());
            case Float v -> (!v.isInfinite()) && (!v.isNaN());
            default -> true;
        };
    }

}
