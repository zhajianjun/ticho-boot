package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiSysException;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-08 23:28
 */
public class TiNumberUtil {

    public static BigDecimal toBigDecimal(Number number) {
        if (null == number) {
            return BigDecimal.ZERO;
        }
        // issue#3423@Github of CVE-2023-51080
        if (isValidNumber(number)) {
            throw new TiSysException("Number is invalid!");
        }
        return switch (number) {
            case BigDecimal bigDecimal -> bigDecimal;
            case Long l -> new BigDecimal(l);
            case Integer integer -> new BigDecimal(integer);
            case BigInteger bigInteger -> new BigDecimal(bigInteger);
            // Float、Double等有精度问题，转换为字符串后再转换
            default -> new BigDecimal(number.toString());
        };

    }

    public static BigDecimal toBigDecimal(String numberStr) {
        if (TiStrUtil.isBlank(numberStr)) {
            return BigDecimal.ZERO;
        }
        if (!TiStrUtil.isNumber(numberStr)) {
            return null;
        }
        return new BigDecimal(numberStr);
    }

    public static boolean isNumber(CharSequence str) {
        return TiStrUtil.isNumber(str);
    }

    public static int parseInt(String number) {
        if (!isNumber(number)) {
            return 0;
        }
        return Integer.parseInt(number);
    }


    public static boolean isValidNumber(Number number) {
        return switch (number) {
            case null -> false;
            case Double v -> (!v.isInfinite()) && (!v.isNaN());
            case Float v -> (!v.isInfinite()) && (!v.isNaN());
            default -> true;
        };
    }

}
