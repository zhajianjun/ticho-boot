package top.ticho.tool.core.exception;

import lombok.Getter;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.enums.TiErrorCode;
import top.ticho.tool.core.enums.TiHttpErrorCode;

/**
 * 基础异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Getter
public class TiBaseException extends RuntimeException {
    /** 错误码 */
    private final int code;
    /** 错误信息 */
    private final String message;

    public TiBaseException(int code, String message) {
        super(getMessage(code, message));
        this.code = code;
        this.message = message;
    }

    public TiBaseException(int code, String message, Throwable throwable) {
        super(getMessage(code, message), throwable);
        this.code = code;
        this.message = message;
    }

    public TiBaseException(String message) {
        super(getMessage(TiHttpErrorCode.INTERNAL_SERVER_ERROR.getCode(), message));
        this.code = TiHttpErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    public TiBaseException(String message, Throwable throwable) {
        super(getMessage(TiHttpErrorCode.INTERNAL_SERVER_ERROR.getCode(), message), throwable);
        this.code = TiHttpErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    public TiBaseException(TiErrorCode errorCode) {
        super(getMessage(errorCode.getCode(), errorCode.getMessage()));
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public TiBaseException(TiErrorCode errorCode, Throwable throwable) {
        super(getMessage(errorCode.getCode(), errorCode.getMessage()), throwable);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public TiBaseException(TiErrorCode errCode, String message) {
        super(getMessage(errCode.getCode(), message));
        this.code = errCode.getCode();
        this.message = message;
    }

    public TiBaseException(TiErrorCode errCode, String message, Throwable throwable) {
        super(getMessage(errCode.getCode(), message), throwable);
        this.code = errCode.getCode();
        this.message = message;
    }

    public static String getMessage(int code, String message) {
        return TiStrUtil.format("错误码: {} 错误信息: {}", code, message);
    }

}
