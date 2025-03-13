package top.ticho.starter.web.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.view.core.TiResult;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.exception.TiBizException;
import top.ticho.starter.view.exception.TiSysException;

import java.util.function.Supplier;

/**
 * api工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiApiUtil {

    public static <T> T getApiResult(Supplier<TiResult<T>> supplier, String errorMsg) {
        return getApi(supplier, errorMsg).getData();
    }

    public static void execute(Supplier<TiResult<?>> supplier, String errorMsg) {
        if (supplier == null) {
            throw new IllegalArgumentException("this param is null");
        }
        TiResult<?> tiResult;
        try {
            tiResult = supplier.get();
        } catch (Exception e) {
            log.error("{},{}", errorMsg, e.getMessage(), e);
            throw new TiSysException(TiBizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        if (tiResult == null) {
            log.error("{},{}", errorMsg, "返回结果为空");
            throw new TiSysException(TiBizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        int code = tiResult.getCode();
        String message = tiResult.getMsg();
        boolean isSuccess = code == TiBizErrCode.SUCCESS.getCode();
        if (!isSuccess) {
            log.error("{},code={},msg={}", errorMsg, code, message);
            throw new TiBizException(code, message);
        }
    }

    public static <T> TiResult<T> getApi(Supplier<TiResult<T>> supplier, String errorMsg) {
        if (supplier == null) {
            throw new IllegalArgumentException("this param is null");
        }
        TiResult<T> tiResult;
        try {
            tiResult = supplier.get();
        } catch (Exception e) {
            log.error("{},{}", errorMsg, e.getMessage(), e);
            throw new TiSysException(TiBizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        if (tiResult == null) {
            log.error("{},{}", errorMsg, "返回结果为空");
            throw new TiSysException(TiBizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        int code = tiResult.getCode();
        String message = tiResult.getMsg();
        boolean isSuccess = code == TiBizErrCode.SUCCESS.getCode();
        if (!isSuccess) {
            log.error("{},code={},msg={}", errorMsg, code, message);
            throw new TiBizException(code, message);
        }
        return tiResult;
    }

}
