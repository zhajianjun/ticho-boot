package top.ticho.tool.core;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.tool.core.enums.TiBizErrorCode;
import top.ticho.tool.core.enums.TiErrorCode;
import top.ticho.tool.core.exception.TiBizException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 断言工具类
 * 提供多种条件判断和异常抛出的便捷方法，用于参数校验和业务逻辑验证
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiAssert {

    /**
     * 断言条件必须为 true
     * 如果条件不为 true，则抛出指定错误码和错误信息的业务异常
     *
     * @param condition 需要判断的条件
     * @param code 错误码
     * @param errorMessage 错误信息
     * @throws TiBizException 当条件不为 true 时抛出
     */
    public static void isTrue(boolean condition, Integer code, String errorMessage) {
        if (!condition) {
            cast(code, errorMessage);
        }
    }

    /**
     * 断言条件必须为 true
     * 如果条件不为 true，则抛出指定错误码的业务异常
     *
     * @param condition 需要判断的条件
     * @param errCode 错误码枚举
     * @throws TiBizException 当条件不为 true 时抛出
     */
    public static void isTrue(boolean condition, TiErrorCode errCode) {
        if (!condition) {
            cast(errCode);
        }
    }

    /**
     * 断言条件必须为 true
     * 如果条件不为 true，则抛出参数错误异常并附带指定消息
     *
     * @param condition 需要判断的条件
     * @param message 错误信息
     * @throws TiBizException 当条件不为 true 时抛出
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言条件必须为 true
     * 如果条件不为 true，则抛出指定错误码和错误信息的业务异常
     *
     * @param condition 需要判断的条件
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 当条件不为 true 时抛出
     */
    public static void isTrue(boolean condition, TiErrorCode errCode, String errorMessage) {
        if (!condition) {
            cast(errCode, errorMessage);
        }
    }

    /**
     * 断言条件必须为 true
     * 如果条件不为 true，则使用 Supplier 懒加载获取错误信息并抛出异常
     *
     * @param condition 需要判断的条件
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当条件不为 true 时抛出
     */
    public static void isTrue(boolean condition, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (!condition) {
            cast(errCode, stringSupplier);
        }
    }

    /**
     * 断言条件必须为 true
     * 如果条件不为 true，则使用默认参数错误码和 Supplier 提供的错误信息抛出异常
     *
     * @param condition 需要判断的条件
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当条件不为 true 时抛出
     */
    public static void isTrue(boolean condition, Supplier<String> stringSupplier) {
        if (!condition) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            cast(errCode, stringSupplier);
        }
    }

    /*  ----------- ------------- */


    /**
     * 断言对象必须为 null
     * 如果对象不为 null，则抛出指定错误码的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @throws TiBizException 当对象不为 null 时抛出
     */
    public static void isNull(Object obj, TiErrorCode errCode) {
        if (Objects.nonNull(obj)) {
            cast(errCode);
        }
    }

    /**
     * 断言对象必须为 null
     * 如果对象不为 null，则抛出参数错误异常并附带指定消息
     *
     * @param obj 需要判断的对象
     * @param message 错误信息
     * @throws TiBizException 当对象不为 null 时抛出
     */
    public static void isNull(Object obj, String message) {
        if (Objects.nonNull(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言对象必须为 null
     * 如果对象不为 null，则抛出指定错误码和错误信息的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 当对象不为 null 时抛出
     */
    public static void isNull(Object obj, TiErrorCode errCode, String errorMessage) {
        if (Objects.nonNull(obj)) {
            cast(errCode, errorMessage);
        }
    }

    /**
     * 断言对象必须为 null
     * 如果对象不为 null，则使用 Supplier 懒加载获取错误信息并抛出异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象不为 null 时抛出
     */
    public static void isNull(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (Objects.nonNull(obj)) {
            cast(errCode, stringSupplier);
        }
    }

    /**
     * 断言对象必须为 null
     * 如果对象不为 null，则使用默认参数错误码和 Supplier 提供的错误信息抛出异常
     *
     * @param obj 需要判断的对象
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象不为 null 时抛出
     */
    public static void isNull(Object obj, Supplier<String> stringSupplier) {
        if (Objects.nonNull(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            cast(errCode, stringSupplier);
        }
    }

    /*  ----------- ------------- */

    /**
     * 断言对象必须不为 null
     * 如果对象为 null，则抛出指定错误码的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @throws TiBizException 当对象为 null 时抛出
     */
    public static void isNotNull(Object obj, TiErrorCode errCode) {
        if (Objects.isNull(obj)) {
            cast(errCode);
        }
    }

    /**
     * 断言对象必须不为 null
     * 如果对象为 null，则抛出参数错误异常并附带指定消息
     *
     * @param obj 需要判断的对象
     * @param message 错误信息
     * @throws TiBizException 当对象为 null 时抛出
     */
    public static void isNotNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言对象必须不为 null
     * 如果对象为 null，则抛出指定错误码和错误信息的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 当对象为 null 时抛出
     */
    public static void isNotNull(Object obj, TiErrorCode errCode, String errorMessage) {
        if (Objects.isNull(obj)) {
            cast(errCode, errorMessage);
        }
    }

    /**
     * 断言对象必须不为 null
     * 如果对象为 null，则使用 Supplier 懒加载获取错误信息并抛出异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象为 null 时抛出
     */
    public static void isNotNull(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (Objects.isNull(obj)) {
            cast(errCode, stringSupplier);
        }
    }

    /**
     * 断言对象必须不为 null
     * 如果对象为 null，则使用默认参数错误码和 Supplier 提供的错误信息抛出异常
     *
     * @param obj 需要判断的对象
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象为 null 时抛出
     */
    public static void isNotNull(Object obj, Supplier<String> stringSupplier) {
        if (Objects.isNull(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            cast(errCode, stringSupplier);
        }
    }

    /*  ----------- ------------- */


    /**
     * 断言对象必须为空
     * 如果对象不为空，则抛出指定错误码的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @throws TiBizException 当对象不为空时抛出
     */
    public static void isEmpty(Object obj, TiErrorCode errCode) {
        if (TiObjUtil.isNotEmpty(obj)) {
            cast(errCode);
        }
    }

    /**
     * 断言对象必须为空
     * 如果对象不为空，则抛出参数错误异常并附带指定消息
     *
     * @param obj 需要判断的对象
     * @param message 错误信息
     * @throws TiBizException 当对象不为空时抛出
     */
    public static void isEmpty(Object obj, String message) {
        if (TiObjUtil.isNotEmpty(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言对象必须为空
     * 如果对象不为空，则抛出指定错误码和错误信息的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 当对象不为空时抛出
     */
    public static void isEmpty(Object obj, TiErrorCode errCode, String errorMessage) {
        if (TiObjUtil.isNotEmpty(obj)) {
            cast(errCode, errorMessage);
        }
    }

    /**
     * 断言对象必须为空
     * 如果对象不为空，则使用 Supplier 懒加载获取错误信息并抛出异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象不为空时抛出
     */
    public static void isEmpty(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (TiObjUtil.isNotEmpty(obj)) {
            cast(errCode, stringSupplier);
        }
    }

    /**
     * 断言对象必须为空
     * 如果对象不为空，则使用默认参数错误码和 Supplier 提供的错误信息抛出异常
     *
     * @param obj 需要判断的对象
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象不为空时抛出
     */
    public static void isEmpty(Object obj, Supplier<String> stringSupplier) {
        if (TiObjUtil.isNotEmpty(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            cast(errCode, stringSupplier);
        }
    }

    /*  ----------- ------------- */

    /**
     * 断言对象必须不为空
     * 如果对象为空，则抛出指定错误码的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @throws TiBizException 当对象为空时抛出
     */
    public static void isNotEmpty(Object obj, TiErrorCode errCode) {
        if (TiObjUtil.isEmpty(obj)) {
            cast(errCode);
        }
    }

    /**
     * 断言对象必须不为空
     * 如果对象为空，则抛出参数错误异常并附带指定消息
     *
     * @param obj 需要判断的对象
     * @param message 错误信息
     * @throws TiBizException 当对象为空时抛出
     */
    public static void isNotEmpty(Object obj, String message) {
        if (TiObjUtil.isEmpty(obj)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言对象必须不为空
     * 如果对象为空，则抛出指定错误码和错误信息的业务异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 当对象为空时抛出
     */
    public static void isNotEmpty(Object obj, TiErrorCode errCode, String errorMessage) {
        if (TiObjUtil.isEmpty(obj)) {
            cast(errCode, errorMessage);
        }
    }

    /**
     * 断言对象必须不为空
     * 如果对象为空，则使用 Supplier 懒加载获取错误信息并抛出异常
     *
     * @param obj 需要判断的对象
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象为空时抛出
     */
    public static void isNotEmpty(Object obj, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (TiObjUtil.isEmpty(obj)) {
            cast(errCode, stringSupplier);
        }
    }

    /**
     * 断言对象必须不为空
     * 如果对象为空，则使用默认参数错误码和 Supplier 提供的错误信息抛出异常
     *
     * @param obj 需要判断的对象
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当对象为空时抛出
     */
    public static void isNotEmpty(Object obj, Supplier<String> stringSupplier) {
        if (TiObjUtil.isEmpty(obj)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            cast(errCode, stringSupplier);
        }
    }

    /*  ----------- ------------- */


    /**
     * 断言字符串必须为空白（null、空字符串或仅包含空白字符）
     * 如果字符串不为空白，则抛出指定错误码的业务异常
     *
     * @param str 需要判断的字符串
     * @param errCode 错误码枚举
     * @throws TiBizException 当字符串不为空白时抛出
     */
    public static void isBlank(String str, TiErrorCode errCode) {
        if (TiStrUtil.isNotBlank(str)) {
            cast(errCode);
        }
    }

    /**
     * 断言字符串必须为空白（null、空字符串或仅包含空白字符）
     * 如果字符串不为空白，则抛出参数错误异常并附带指定消息
     *
     * @param str 需要判断的字符串
     * @param message 错误信息
     * @throws TiBizException 当字符串不为空白时抛出
     */
    public static void isBlank(String str, String message) {
        if (TiStrUtil.isNotBlank(str)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言字符串必须为空白（null、空字符串或仅包含空白字符）
     * 如果字符串不为空白，则抛出指定错误码和错误信息的业务异常
     *
     * @param str 需要判断的字符串
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 当字符串不为空白时抛出
     */
    public static void isBlank(String str, TiErrorCode errCode, String errorMessage) {
        if (TiStrUtil.isNotBlank(str)) {
            cast(errCode, errorMessage);
        }
    }

    /**
     * 断言字符串必须为空白（null、空字符串或仅包含空白字符）
     * 如果字符串不为空白，则使用 Supplier 懒加载获取错误信息并抛出异常
     *
     * @param str 需要判断的字符串
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当字符串不为空白时抛出
     */
    public static void isBlank(String str, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (TiStrUtil.isNotBlank(str)) {
            cast(errCode, stringSupplier);
        }
    }

    /**
     * 断言字符串必须为空白（null、空字符串或仅包含空白字符）
     * 如果字符串不为空白，则使用默认参数错误码和 Supplier 提供的错误信息抛出异常
     *
     * @param str 需要判断的字符串
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当字符串不为空白时抛出
     */
    public static void isBlank(String str, Supplier<String> stringSupplier) {
        if (TiStrUtil.isNotBlank(str)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            cast(errCode, stringSupplier);
        }
    }

    /*  ----------- ------------- */

    /**
     * 断言字符串必须不为空白（非 null、非空且包含非空白字符）
     * 如果字符串为空白，则抛出指定错误码的业务异常
     *
     * @param obj 需要判断的字符串
     * @param errCode 错误码枚举
     * @throws TiBizException 当字符串为空白时抛出
     */
    public static void isNotBlank(String obj, TiErrorCode errCode) {
        if (TiStrUtil.isBlank(obj)) {
            cast(errCode);
        }
    }

    /**
     * 断言字符串必须不为空白（非 null、非空且包含非空白字符）
     * 如果字符串为空白，则抛出参数错误异常并附带指定消息
     *
     * @param str 需要判断的字符串
     * @param message 错误信息
     * @throws TiBizException 当字符串为空白时抛出
     */
    public static void isNotBlank(String str, String message) {
        if (TiStrUtil.isBlank(str)) {
            cast(TiBizErrorCode.PARAM_ERROR, message);
        }
    }

    /**
     * 断言字符串必须不为空白（非 null、非空且包含非空白字符）
     * 如果字符串为空白，则抛出指定错误码和错误信息的业务异常
     *
     * @param obj 需要判断的字符串
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 当字符串为空白时抛出
     */
    public static void isNotBlank(String obj, TiErrorCode errCode, String errorMessage) {
        if (TiStrUtil.isBlank(obj)) {
            cast(errCode, errorMessage);
        }
    }

    /**
     * 断言字符串必须不为空白（非 null、非空且包含非空白字符）
     * 如果字符串为空白，则使用 Supplier 懒加载获取错误信息并抛出异常
     *
     * @param str 需要判断的字符串
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当字符串为空白时抛出
     */
    public static void isNotBlank(String str, TiErrorCode errCode, Supplier<String> stringSupplier) {
        if (TiStrUtil.isBlank(str)) {
            cast(errCode, stringSupplier);
        }
    }

    /**
     * 断言字符串必须不为空白（非 null、非空且包含非空白字符）
     * 如果字符串为空白，则使用默认参数错误码和 Supplier 提供的错误信息抛出异常
     *
     * @param str 需要判断的字符串
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 当字符串为空白时抛出
     */
    public static void isNotBlank(String str, Supplier<String> stringSupplier) {
        if (TiStrUtil.isBlank(str)) {
            TiErrorCode errCode = TiBizErrorCode.PARAM_ERROR;
            cast(errCode, stringSupplier);
        }
    }

    /*  ----------- ------------- */

    /**
     * 直接抛出指定错误码的业务异常
     *
     * @param errCode 错误码枚举
     * @throws TiBizException 总是抛出
     */
    public static void cast(TiErrorCode errCode) {
        throw new TiBizException(errCode);
    }

    /**
     * 直接抛出指定错误码和错误信息的业务异常
     *
     * @param errCode 错误码枚举
     * @param errorMessage 错误信息
     * @throws TiBizException 总是抛出
     */
    public static void cast(TiErrorCode errCode, String errorMessage) {
        throw new TiBizException(errCode, errorMessage);
    }

    /**
     * 直接抛出业务异常，支持使用 Supplier 懒加载错误信息
     *
     * @param errCode 错误码枚举
     * @param stringSupplier 错误信息提供者（懒加载）
     * @throws TiBizException 总是抛出
     */
    public static void cast(TiErrorCode errCode, Supplier<String> stringSupplier) {
        String errorMessage = Optional.ofNullable(stringSupplier).map(Supplier::get).orElse(errCode.getMessage());
        throw new TiBizException(errCode, errorMessage);
    }

    /**
     * 直接抛出指定错误码和错误信息的业务异常
     *
     * @param code 错误码
     * @param errorMessage 错误信息
     * @throws TiBizException 总是抛出
     */
    public static void cast(Integer code, String errorMessage) {
        throw new TiBizException(code, errorMessage);
    }

}
