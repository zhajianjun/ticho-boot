package top.ticho.boot.web.util;

import lombok.extern.slf4j.Slf4j;
import top.ticho.boot.view.core.Result;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.exception.BizException;
import top.ticho.boot.view.exception.SysException;

import java.util.function.Supplier;

/**
 * api工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Slf4j
public class ApiUtil {
    private ApiUtil() {

    }

    public static <T> T getApiResult(Supplier<Result<T>> supplier, String errorMsg) {
        return getApi(supplier, errorMsg).getData();
    }

    public static void execute(Supplier<Result<?>> supplier, String errorMsg) {
        if (supplier == null) {
            throw new IllegalArgumentException("this param is null");
        }
        Result<?> result;
        try {
            result = supplier.get();
        } catch (Exception e) {
            log.error("{},{}", errorMsg, e.getMessage(), e);
            throw new SysException(BizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        if (result == null) {
            log.error("{},{}", errorMsg, "返回结果为空");
            throw new SysException(BizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        int code = result.getCode();
        String message = result.getMsg();
        boolean isSuccess = code == BizErrCode.SUCCESS.getCode();
        if (!isSuccess) {
            log.error("{},code={},msg={}", errorMsg, code, message);
            throw new BizException(code, message);
        }
    }

    public static <T> Result<T> getApi(Supplier<Result<T>> supplier, String errorMsg) {
        if (supplier == null) {
            throw new IllegalArgumentException("this param is null");
        }
        Result<T> result;
        try {
            result = supplier.get();
        } catch (Exception e) {
            log.error("{},{}", errorMsg, e.getMessage(), e);
            throw new SysException(BizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        if (result == null) {
            log.error("{},{}", errorMsg, "返回结果为空");
            throw new SysException(BizErrCode.APP_SERVICE_ERR, errorMsg);
        }
        int code = result.getCode();
        String message = result.getMsg();
        boolean isSuccess = code == BizErrCode.SUCCESS.getCode();
        if (!isSuccess) {
            log.error("{},code={},msg={}", errorMsg, code, message);
            throw new BizException(code, message);
        }
        return result;
    }
}
