package top.ticho.tool.core;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-08 23:28
 */
public class TiNumberUtil {

    public static BigDecimal toBigDecimal(Number number) {
        return NumberUtil.toBigDecimal(number);
    }

    public static BigDecimal toBigDecimal(String numberStr) {
        return NumberUtil.toBigDecimal(numberStr);
    }

    public static boolean isNumber(CharSequence str) {
        return NumberUtil.isNumber(str);
    }

    public static int parseInt(String number) {
        return NumberUtil.parseInt(number);
    }


}
