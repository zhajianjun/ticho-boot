package top.ticho.starter.view.exception;

import lombok.Getter;
import top.ticho.starter.view.enums.TiErrCode;
import top.ticho.starter.view.enums.TiHttpErrCode;

/**
 * 基础异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
public class TiBaseException extends RuntimeException {
    /** 状态码 */
    private final int code;
    /** 状态信息 */
    private final String msg;

    public TiBaseException(int code, String msg) {
        super(getMessage(code, msg));
        this.code = code;
        this.msg = msg;
    }

    public TiBaseException(String msg) {
        super(getMessage(TiHttpErrCode.INTERNAL_SERVER_ERROR.getCode(), msg));
        this.code = TiHttpErrCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = msg;
    }

    public TiBaseException(TiErrCode errorCode) {
        super(getMessage(errorCode.getCode(), errorCode.getMsg()));
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public TiBaseException(TiErrCode errCode, String msg) {
        super(getMessage(errCode.getCode(), msg));
        this.code = errCode.getCode();
        this.msg = msg;
    }

    private static String getMessage(int code, String msg) {
        return String.format("异常代码:%s,异常信息:%s", code, msg);
    }

}
