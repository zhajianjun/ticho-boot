package top.ticho.starter.web.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.view.core.TiResult;
import top.ticho.tool.core.enums.TiBizErrorCode;
import top.ticho.tool.core.exception.TiBizException;
import top.ticho.tool.core.exception.TiSysException;

import java.util.function.Supplier;

/**
 * api工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiApiUtil {

    public static <T> T getApiResult(Supplier<TiResult<T>> supplier, String errorMsg) {
        return getApi(supplier, errorMsg).getData();
    }

    public static <T> void execute(Supplier<TiResult<T>> supplier, String errorMsg) {
        handleApiCall(supplier, errorMsg);
    }

    public static <T> TiResult<T> getApi(Supplier<TiResult<T>> supplier, String errorMsg) {
        return handleApiCall(supplier, errorMsg);
    }

    /**
     * 统一处理API调用逻辑
     */
    private static <T> TiResult<T> handleApiCall(Supplier<TiResult<T>> supplier, String errorMsg) {
        if (supplier == null) {
            throw new IllegalArgumentException("this param is null");
        }
        TiResult<T> tiResult;
        try {
            tiResult = supplier.get();
        } catch (Exception e) {
            throw new TiSysException(TiBizErrorCode.APP_SERVICE_ERR, errorMsg, e);
        }
        if (tiResult == null) {
            log.error("{},{}", errorMsg, "返回结果为空");
            throw new TiSysException(TiBizErrorCode.APP_SERVICE_ERR, errorMsg);
        }
        int code = tiResult.getCode();
        String message = tiResult.getMessage();
        boolean isSuccess = code == TiBizErrorCode.SUCCESS.getCode();
        if (!isSuccess) {
            log.error("{},code={},message={}", errorMsg, code, message);
            throw new TiBizException(code, message);
        }
        return tiResult;
    }

}
