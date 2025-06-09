package top.ticho.starter.view.util;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.starter.view.enums.TiBizErrorCode;
import top.ticho.starter.view.enums.TiErrorCode;
import top.ticho.starter.view.exception.TiBizException;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 断言工具类
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiAssert {

    public static void isTrue(boolean condition, Integer code, String errorMessage) {
        if (!condition) {
            cast(code, errorMessage);
        }
    }

    public static void isTrue(boolean condition, TiErrorCode errCode) {
        if (!condition) {
            cast(errCode);
        }
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    public static void isTrue(boolean condition, TiErrorCode errCode, String errorMessage) {
        if (!condition) {
            cast(errCode, errorMessage);
        }
    }

    public static void isTrue(boolean condition, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (!condition) {
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    public static void isTrue(boolean condition, Supplier<String> stringSupplier) {
        if (!condition) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    /*  ----------- ------------- */


    public static void isNull(Object obj, TiErrorCode errCode) {
        if (ObjUtil.isNotNull(obj)) {
            cast(errCode);
        }
    }

    public static void isNull(Object obj, String message) {
        if (ObjUtil.isNotNull(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    public static void isNull(Object obj, TiErrorCode errCode, String errorMessage) {
        if (ObjUtil.isNotNull(obj)) {
            cast(errCode, errorMessage);
        }
    }

    public static void isNull(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotNull(obj)) {
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    public static void isNull(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotNull(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    /*  ----------- ------------- */

    public static void isNotNull(Object obj, TiErrorCode errCode) {
        if (ObjUtil.isNull(obj)) {
            cast(errCode);
        }
    }

    public static void isNotNull(Object obj, String message) {
        if (ObjUtil.isNull(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    public static void isNotNull(Object obj, TiErrorCode errCode, String errorMessage) {
        if (ObjUtil.isNull(obj)) {
            cast(errCode, errorMessage);
        }
    }

    public static void isNotNull(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isNull(obj)) {
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    public static void isNotNull(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isNull(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    /*  ----------- ------------- */


    public static void isEmpty(Object obj, TiErrorCode errCode) {
        if (ObjUtil.isNotEmpty(obj)) {
            cast(errCode);
        }
    }

    public static void isEmpty(Object obj, String message) {
        if (ObjUtil.isNotEmpty(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    public static void isEmpty(Object obj, TiErrorCode errCode, String errorMessage) {
        if (ObjUtil.isNotEmpty(obj)) {
            cast(errCode, errorMessage);
        }
    }

    public static void isEmpty(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotEmpty(obj)) {
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    public static void isEmpty(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotEmpty(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    /*  ----------- ------------- */

    public static void isNotEmpty(Object obj, TiErrorCode errCode) {
        if (ObjUtil.isEmpty(obj)) {
            cast(errCode);
        }
    }

    public static void isNotEmpty(Object obj, String message) {
        if (ObjUtil.isEmpty(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    public static void isNotEmpty(Object obj, TiErrorCode errCode, String errorMessage) {
        if (ObjUtil.isEmpty(obj)) {
            cast(errCode, errorMessage);
        }
    }

    public static void isNotEmpty(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isEmpty(obj)) {
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    public static void isNotEmpty(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isEmpty(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    /*  ----------- ------------- */


    public static void isBlank(String str, TiErrorCode errCode) {
        if (StrUtil.isNotBlank(str)) {
            cast(errCode);
        }
    }

    public static void isBlank(String str, String message) {
        if (StrUtil.isNotBlank(str)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    public static void isBlank(String str, TiErrorCode errCode, String errorMessage) {
        if (StrUtil.isNotBlank(str)) {
            cast(errCode, errorMessage);
        }
    }

    public static void isBlank(String str, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (StrUtil.isNotBlank(str)) {
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    public static void isBlank(String str, Supplier<String> stringSupplier) {
        if (StrUtil.isNotBlank(str)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    /*  ----------- ------------- */

    public static void isNotBlank(String obj, TiErrorCode errCode) {
        if (StrUtil.isBlank(obj)) {
            cast(errCode);
        }
    }

    public static void isNotBlank(String str, String message) {
        if (StrUtil.isBlank(str)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    public static void isNotBlank(String obj, TiErrorCode errCode, String errorMessage) {
        if (StrUtil.isBlank(obj)) {
            cast(errCode, errorMessage);
        }
    }

    public static void isNotBlank(String str, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (StrUtil.isBlank(str)) {
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    public static void isNotBlank(String str, Supplier<String> stringSupplier) {
        if (StrUtil.isBlank(str)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
            cast(errCode, errorMessage);
        }
    }

    /*  ----------- ------------- */

    public static void cast(TiErrorCode errCode) {
        throw new TiBizException(errCode);
    }

    public static void cast(TiErrorCode errCode, String errorMessage) {
        throw new TiBizException(errCode, errorMessage);
    }

    public static void cast(TiErrorCode errCode, Supplier<String> stringSupplier) {
        String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
        throw new TiBizException(errCode, errorMessage);
    }

    public static void cast(Integer code, String errorMessage) {
        throw new TiBizException(code, errorMessage);
    }

    public static TiBizException getException(TiErrorCode errCode) {
        return new TiBizException(errCode.getCode(), errCode.getMessage());
    }

}
