package top.ticho.boot.view.exception;

import lombok.Getter;
import top.ticho.boot.view.enums.TiErrCode;

/**
 * 系统异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
public class TiSysException extends TiBaseException {

    public TiSysException(int code, String msg) {
        super(code, msg);
    }

    public TiSysException(String msg) {
        super(msg);
    }

    public TiSysException(TiErrCode resultCode) {
        super(resultCode);
    }

    public TiSysException(TiErrCode resultCode, String msg) {
        super(resultCode, msg);
    }

}
