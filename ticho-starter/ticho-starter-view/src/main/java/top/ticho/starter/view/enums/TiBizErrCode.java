package top.ticho.boot.view.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 通用业务错误码
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Getter
@AllArgsConstructor
@ToString
public enum TiBizErrCode implements Serializable, TiErrCode {

    SUCCESS(0, "操作成功"),
    FAIL(-1, "执行失败"),
    PARAM_ERROR(1000, "参数不能为空"),
    IS_NOT_EXISTS(1001, "数据不存在"),
    APP_SERVICE_ERR(1001, "数据不存在");

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private final int code;

    /** 状态信息 */
    private final String msg;

}
