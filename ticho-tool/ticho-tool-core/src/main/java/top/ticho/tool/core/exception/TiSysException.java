package top.ticho.tool.core.exception;

import lombok.Getter;
import top.ticho.tool.core.TiExceptionUtil;
import top.ticho.tool.core.enums.TiErrorCode;

/**
 * 系统异常处理
 *
 * @author zhajianjun
 * @date 2025-10-19 13:10
 */
@Getter
public class TiSysException extends TiBaseException {

    public TiSysException(int code, String message) {
        super(code, message);
    }

    public TiSysException(int code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public TiSysException(String message) {
        super(message);
    }

    public TiSysException(Throwable e) {
        super(TiExceptionUtil.getMessage(e), e);
    }

    public TiSysException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TiSysException(TiErrorCode resultCode) {
        super(resultCode);
    }

    public TiSysException(TiErrorCode resultCode, Throwable throwable) {
        super(resultCode, throwable);
    }

    public TiSysException(TiErrorCode resultCode, String message) {
        super(resultCode, message);
    }

    public TiSysException(TiErrorCode resultCode, String message, Throwable throwable) {
        super(resultCode, message, throwable);
    }

}
