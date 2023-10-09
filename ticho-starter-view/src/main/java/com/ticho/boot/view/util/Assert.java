package com.ticho.boot.view.util;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.view.enums.BizErrCode;
import com.ticho.boot.view.enums.IErrCode;
import com.ticho.boot.view.exception.BizException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 断言工具类
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Assert {

    public static void isTrue(boolean condition, Integer code, String errMsg) {
        if (!condition) {
            cast(code, errMsg);
        }
    }

    public static void isTrue(boolean condition, IErrCode errCode) {
        if (!condition) {
            cast(errCode);
        }
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            cast(BizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isTrue(boolean condition, IErrCode errCode, String errMsg) {
        if (!condition) {
            cast(errCode, errMsg);
        }
    }

    public static void isTrue(boolean condition, IErrCode errCode, Supplier<String> stringSupplier) {
        if (!condition) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isTrue(boolean condition, Supplier<String> stringSupplier) {
        if (!condition) {
            IErrCode errCode = BizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */


    public static void isNull(Object obj, IErrCode errCode) {
        if (ObjectUtil.isNotNull(obj)) {
            cast(errCode);
        }
    }

    public static void isNull(Object obj, String message) {
        if (ObjectUtil.isNotNull(obj)) {
            cast(BizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNull(Object obj, IErrCode errCode, String errMsg) {
        if (ObjectUtil.isNotNull(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNull(Object obj, IErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjectUtil.isNotNull(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNull(Object obj, Supplier<String> stringSupplier) {
        if (ObjectUtil.isNotNull(obj)) {
            IErrCode errCode = BizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void isNotNull(Object obj, IErrCode errCode) {
        if (ObjectUtil.isNull(obj)) {
            cast(errCode);
        }
    }

    public static void isNotNull(Object obj, String message) {
        if (ObjectUtil.isNull(obj)) {
            cast(BizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNotNull(Object obj, IErrCode errCode, String errMsg) {
        if (ObjectUtil.isNull(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNotNull(Object obj, IErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjectUtil.isNull(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNotNull(Object obj, Supplier<String> stringSupplier) {
        if (ObjectUtil.isNull(obj)) {
            IErrCode errCode = BizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */


    public static void isEmpty(Object obj, IErrCode errCode) {
        if (ObjectUtil.isNotEmpty(obj)) {
            cast(errCode);
        }
    }

    public static void isEmpty(Object obj, String message) {
        if (ObjectUtil.isNotEmpty(obj)) {
            cast(BizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isEmpty(Object obj, IErrCode errCode, String errMsg) {
        if (ObjectUtil.isNotEmpty(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isEmpty(Object obj, IErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjectUtil.isNotEmpty(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isEmpty(Object obj, Supplier<String> stringSupplier) {
        if (ObjectUtil.isNotEmpty(obj)) {
            IErrCode errCode = BizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void isNotEmpty(Object obj, IErrCode errCode) {
        if (ObjectUtil.isEmpty(obj)) {
            cast(errCode);
        }
    }

    public static void isNotEmpty(Object obj, String message) {
        if (ObjectUtil.isEmpty(obj)) {
            cast(BizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNotEmpty(Object obj, IErrCode errCode, String errMsg) {
        if (ObjectUtil.isEmpty(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNotEmpty(Object obj, IErrCode errCode, Supplier<String> stringSupplier) {
        if (ObjectUtil.isEmpty(obj)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNotEmpty(Object obj, Supplier<String> stringSupplier) {
        if (ObjectUtil.isEmpty(obj)) {
            IErrCode errCode = BizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */


    public static void isBlank(String str, IErrCode errCode) {
        if (StrUtil.isNotBlank(str)) {
            cast(errCode);
        }
    }

    public static void isBlank(String str, String message) {
        if (StrUtil.isNotBlank(str)) {
            cast(BizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isBlank(String str, IErrCode errCode, String errMsg) {
        if (StrUtil.isNotBlank(str)) {
            cast(errCode, errMsg);
        }
    }

    public static void isBlank(String str, IErrCode errCode, Supplier<String> stringSupplier) {
        if (StrUtil.isNotBlank(str)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isBlank(String str, Supplier<String> stringSupplier) {
        if (StrUtil.isNotBlank(str)) {
            IErrCode errCode = BizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void isNotBlank(String obj, IErrCode errCode) {
        if (StrUtil.isBlank(obj)) {
            cast(errCode);
        }
    }

    public static void isNotBlank(String str, String message) {
        if (StrUtil.isBlank(str)) {
            cast(BizErrCode.PARAM_ERROR, message);
        }
    }

    public static void isNotBlank(String obj, IErrCode errCode, String errMsg) {
        if (StrUtil.isBlank(obj)) {
            cast(errCode, errMsg);
        }
    }

    public static void isNotBlank(String str, IErrCode errCode, Supplier<String> stringSupplier) {
        if (StrUtil.isBlank(str)) {
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    public static void isNotBlank(String str, Supplier<String> stringSupplier) {
        if (StrUtil.isBlank(str)) {
            IErrCode errCode = BizErrCode.PARAM_ERROR;
            String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
            cast(errCode, errMsg);
        }
    }

    /*  ----------- ------------- */

    public static void cast(IErrCode errCode) {
        throw new BizException(errCode);
    }

    public static void cast(IErrCode errCode, String errMsg) {
        throw new BizException(errCode, errMsg);
    }

    public static void cast(IErrCode errCode, Supplier<String> stringSupplier) {
        String errMsg = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMsg());
        throw new BizException(errCode, errMsg);
    }

    public static void cast(Integer code, String errMsg) {
        throw new BizException(code, errMsg);
    }

    public static BizException getException(IErrCode errCode) {
        return new BizException(errCode.getCode(), errCode.getMsg());
    }

}
