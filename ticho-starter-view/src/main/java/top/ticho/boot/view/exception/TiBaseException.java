package top.ticho.boot.view.exception;

import lombok.Getter;
import top.ticho.boot.view.enums.TiHttpErrCode;
import top.ticho.boot.view.enums.TiErrCode;

/**
 * 基础异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
public class TiBaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_CODE = "异常代码:";
    public static final String ERROR_MSG = ",异常信息:";

    /** 状态码 */
    private final int code;

    /** 状态信息 */
    private final String msg;

    public TiBaseException(int code, String msg) {
        super(ERROR_CODE + code + ERROR_MSG + msg);
        this.code = code;
        this.msg = msg;
    }

    public TiBaseException(String msg) {
        super(ERROR_CODE + TiHttpErrCode.INTERNAL_SERVER_ERROR.getCode() + ERROR_MSG + msg);
        this.code = TiHttpErrCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = msg;
    }

    public TiBaseException(TiErrCode errorCode) {
        super(ERROR_CODE + errorCode.getCode() + ERROR_MSG + errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public TiBaseException(TiErrCode errCode, String msg) {
        super(ERROR_CODE + errCode.getCode() + ERROR_MSG + msg);
        this.code = errCode.getCode();
        this.msg = msg;
    }

}
