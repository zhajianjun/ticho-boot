package top.ticho.tool.core.exception;

import lombok.Getter;
import top.ticho.tool.core.TiExceptionUtil;
import top.ticho.tool.core.enums.TiErrorCode;

/**
 * 工具类异常
 *
 * @author zhajianjun
 * @date 2025-12-27 12:10
 */
@Getter
public class TiUtilException extends TiBaseException {

    public TiUtilException(int code, String message) {
        super(code, message);
    }

    public TiUtilException(int code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public TiUtilException(String message) {
        super(message);
    }

    public TiUtilException(Throwable e) {
        super(TiExceptionUtil.getMessage(e), e);
    }

    public TiUtilException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TiUtilException(TiErrorCode resultCode) {
        super(resultCode);
    }

    public TiUtilException(TiErrorCode resultCode, Throwable throwable) {
        super(resultCode, throwable);
    }

    public TiUtilException(TiErrorCode resultCode, String message) {
        super(resultCode, message);
    }

    public TiUtilException(TiErrorCode resultCode, String message, Throwable throwable) {
        super(resultCode, message, throwable);
    }

}
