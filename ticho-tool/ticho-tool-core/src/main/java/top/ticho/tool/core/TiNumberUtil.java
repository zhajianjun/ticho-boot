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

}
