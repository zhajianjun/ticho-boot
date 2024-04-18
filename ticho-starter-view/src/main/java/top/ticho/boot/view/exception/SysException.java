package top.ticho.boot.view.exception;

import lombok.Getter;
import top.ticho.boot.view.enums.IErrCode;

/**
 * 系统异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
public class SysException extends BaseException {
    private static final long serialVersionUID = 1L;


    public SysException(int code, String msg) {
        super(code, msg);
    }

    public SysException(String msg) {
        super(msg);
    }

    public SysException(IErrCode resultCode) {
        super(resultCode);
    }


    public SysException(IErrCode resultCode, String msg) {
        super(resultCode, msg);
    }

}
