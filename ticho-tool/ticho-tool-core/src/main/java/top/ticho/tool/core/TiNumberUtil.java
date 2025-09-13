package top.ticho.tool.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import top.ticho.tool.core.exception.TiUtilException;

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
            throw new TiUtilException("Number is invalid!");
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
        if (StringUtils.isBlank(numberStr)) {
            return BigDecimal.ZERO;
        }
        if (!StringUtils.isNumeric(numberStr)) {
            return null;
        }
        return new BigDecimal(numberStr);
    }

    public static boolean isNumber(CharSequence str) {
        return StringUtils.isNumeric(str);
    }

    public static int parseInt(String number) {
        return NumberUtils.toInt(number);
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
