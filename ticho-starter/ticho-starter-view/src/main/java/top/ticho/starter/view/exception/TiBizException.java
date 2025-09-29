package top.ticho.starter.view.exception;

import lombok.Getter;
import top.ticho.starter.view.enums.TiErrorCode;

/**
 * 业务异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Getter
public class TiBizException extends TiBaseException {

    public TiBizException(int code, String message) {
        super(code, message);
    }

    public TiBizException(int code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public TiBizException(String message) {
        super(message);
    }

    public TiBizException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TiBizException(TiErrorCode resultCode) {
        super(resultCode);
    }

    public TiBizException(TiErrorCode resultCode, Throwable throwable) {
        super(resultCode, throwable);
    }

    public TiBizException(TiErrorCode resultCode, String message) {
        super(resultCode, message);
    }

    public TiBizException(TiErrorCode resultCode, String message, Throwable throwable) {
        super(resultCode, message, throwable);
    }

}
