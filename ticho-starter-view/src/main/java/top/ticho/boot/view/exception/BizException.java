package top.ticho.boot.view.exception;

import lombok.Getter;
import top.ticho.boot.view.enums.IErrCode;

/**
 * 业务异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
public class BizException extends BaseException {
    private static final long serialVersionUID = 1L;

    public BizException(int code, String msg) {
        super(code, msg);
    }

    public BizException(String msg) {
        super(msg);
    }

    public BizException(IErrCode resultCode) {
        super(resultCode);
    }


    public BizException(IErrCode resultCode, String msg) {
        super(resultCode, msg);
    }

}
