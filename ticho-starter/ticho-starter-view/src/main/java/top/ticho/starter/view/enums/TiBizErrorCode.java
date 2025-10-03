package top.ticho.starter.view.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用业务错误码
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Getter
@AllArgsConstructor
@ToString
public enum TiBizErrorCode implements Serializable, TiErrorCode {

    SUCCESS(0, "操作成功"),
    FAIL(-1, "操作失败"),
    PARAM_ERROR(1000, "参数异常"),
    IS_NOT_EXISTS(1001, "参数不存在"),
    APP_SERVICE_ERR(1001, "服务异常");

    @Serial
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private final int code;

    /** 状态信息 */
    private final String message;

}
