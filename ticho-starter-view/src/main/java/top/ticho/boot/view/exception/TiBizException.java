package top.ticho.boot.view.exception;

import lombok.Getter;
import top.ticho.boot.view.enums.TiErrCode;

/**
 * 业务异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
public class TiBizException extends TiBaseException {
    private static final long serialVersionUID = 1L;

    public TiBizException(int code, String msg) {
        super(code, msg);
    }

    public TiBizException(String msg) {
        super(msg);
    }

    public TiBizException(TiErrCode resultCode) {
        super(resultCode);
    }

    public TiBizException(TiErrCode resultCode, String msg) {
        super(resultCode, msg);
    }

}
