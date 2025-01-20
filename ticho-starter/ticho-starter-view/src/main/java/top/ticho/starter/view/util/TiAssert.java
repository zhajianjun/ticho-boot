package top.ticho.starter.view.util;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.enums.TiErrCode;
import top.ticho.starter.view.exception.TiBizException;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 断言工具类
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiAssert {

    public static void isTrue(boolean condition, Integer code, String errMsg) {
        if (!condition) {
            cast(code, errMsg);
        }
    }

    public static void isTrue(boolean condition, TiErrCode errCode) {
        if (!condition) {
            cast(errCode);
        }
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            cast(TiBizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isTrue(boolean condition, TiErrCode errCode, String errMsg) {
        if (!condition) {
            cast(errCode, errMsg);
        }
    }

    public static void isTrue(boolean condition, TiErrCode errCode, Supplier<String> stringSupplier) {
        if (!condition) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isTrue(boolean condition, Supplier<String> stringSupplier) {
        if (!condition) {
            TiErrCode errCode = TiBizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */


    public static void isNull(Object obj, TiErrCode errCode) {
        if (ObjUtil.isNotNull(obj)) {
            cast(errCode);
        }
    }

    public static void isNull(Object obj, String message) {
        if (ObjUtil.isNotNull(obj)) {
            cast(TiBizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNull(Object obj, TiErrCode errCode, String errMsg) {
        if (ObjUtil.isNotNull(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNull(Object obj, TiErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotNull(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNull(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotNull(obj)) {
            TiErrCode errCode = TiBizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void isNotNull(Object obj, TiErrCode errCode) {
        if (ObjUtil.isNull(obj)) {
            cast(errCode);
        }
    }

    public static void isNotNull(Object obj, String message) {
        if (ObjUtil.isNull(obj)) {
            cast(TiBizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNotNull(Object obj, TiErrCode errCode, String errMsg) {
        if (ObjUtil.isNull(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNotNull(Object obj, TiErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isNull(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNotNull(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isNull(obj)) {
            TiErrCode errCode = TiBizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */


    public static void isEmpty(Object obj, TiErrCode errCode) {
        if (ObjUtil.isNotEmpty(obj)) {
            cast(errCode);
        }
    }

    public static void isEmpty(Object obj, String message) {
        if (ObjUtil.isNotEmpty(obj)) {
            cast(TiBizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isEmpty(Object obj, TiErrCode errCode, String errMsg) {
        if (ObjUtil.isNotEmpty(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isEmpty(Object obj, TiErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotEmpty(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isEmpty(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isNotEmpty(obj)) {
            TiErrCode errCode = TiBizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void isNotEmpty(Object obj, TiErrCode errCode) {
        if (ObjUtil.isEmpty(obj)) {
            cast(errCode);
        }
    }

    public static void isNotEmpty(Object obj, String message) {
        if (ObjUtil.isEmpty(obj)) {
            cast(TiBizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNotEmpty(Object obj, TiErrCode errCode, String errMsg) {
        if (ObjUtil.isEmpty(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNotEmpty(Object obj, TiErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjUtil.isEmpty(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNotEmpty(Object obj, Supplier<String> stringSupplier) {
        if (ObjUtil.isEmpty(obj)) {
            TiErrCode errCode = TiBizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */


    public static void isBlank(String str, TiErrCode errCode) {
        if (StrUtil.isNotBlank(str)) {
            cast(errCode);
        }
    }

    public static void isBlank(String str, String message) {
        if (StrUtil.isNotBlank(str)) {
            cast(TiBizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isBlank(String str, TiErrCode errCode, String errMsg) {
        if (StrUtil.isNotBlank(str)) {
            cast(errCode, errMsg);
        }
    }

    public static void isBlank(String str, TiErrCode errCode, Supplier<String> stringSupplier) {
        if (StrUtil.isNotBlank(str)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isBlank(String str, Supplier<String> stringSupplier) {
        if (StrUtil.isNotBlank(str)) {
            TiErrCode errCode = TiBizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void isNotBlank(String obj, TiErrCode errCode) {
        if (StrUtil.isBlank(obj)) {
            cast(errCode);
        }
    }

    public static void isNotBlank(String str, String message) {
        if (StrUtil.isBlank(str)) {
            cast(TiBizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNotBlank(String obj, TiErrCode errCode, String errMsg) {
        if (StrUtil.isBlank(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNotBlank(String str, TiErrCode errCode, Supplier<String> stringSupplier) {
        if (StrUtil.isBlank(str)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNotBlank(String str, Supplier<String> stringSupplier) {
        if (StrUtil.isBlank(str)) {
            TiErrCode errCode = TiBizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void cast(TiErrCode errCode) {
        throw new TiBizException(errCode);
    }

    public static void cast(TiErrCode errCode, String errMsg) {
        throw new TiBizException(errCode, errMsg);
    }

    public static void cast(TiErrCode errCode, Supplier<String> stringSupplier) {
        String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
        throw new TiBizException(errCode, errMsg);
    }

    public static void cast(Integer code, String errMsg) {
        throw new TiBizException(code, errMsg);
    }

    public static TiBizException getException(TiErrCode errCode) {
        return new TiBizException(errCode.getCode(), errCode.getMsg());
    }

}
