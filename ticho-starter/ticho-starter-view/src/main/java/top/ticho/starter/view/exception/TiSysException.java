package top.ticho.starter.view.exception;

import lombok.Getter;
import top.ticho.starter.view.enums.TiErrorCode;

/**
 * 系统异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Getter
public class TiSysException extends TiBaseException {

    public TiSysException(int code, String message) {
        super(code, message);
    }

    public TiSysException(String message) {
        super(message);
    }

    public TiSysException(TiErrorCode resultCode) {
        super(resultCode);
    }

    public TiSysException(TiErrorCode resultCode, String message) {
        super(resultCode, message);
    }

}
