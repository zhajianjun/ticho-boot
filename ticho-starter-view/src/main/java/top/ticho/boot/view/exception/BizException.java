package top.ticho.boot.view.exception;

import top.ticho.boot.view.enums.HttpErrCode;
import top.ticho.boot.view.enums.IErrCode;
import lombok.Getter;

/**
 * 业务异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_CODE = "异常代码:";
    public static final String ERROR_MSG = ",异常信息:";

    /**
     * 状态码
     */
    private final int code;

    /**
     *状态信息
     */
    private final String msg;

    public BizException(int code, String msg) {
        super(ERROR_CODE + code + ERROR_MSG + msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(String msg) {
        super(ERROR_CODE + HttpErrCode.INTERNAL_SERVER_ERROR.getCode() + ERROR_MSG + msg);
        this.code = HttpErrCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = msg;
    }

    public BizException(IErrCode resultCode) {
        super(ERROR_CODE + resultCode.getCode() + ERROR_MSG + resultCode.getMsg());
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }


    public BizException(IErrCode resultCode, String msg) {
        super(ERROR_CODE + resultCode.getCode() + ERROR_MSG + msg);
        this.code = resultCode.getCode();
        this.msg = msg;
    }
}
