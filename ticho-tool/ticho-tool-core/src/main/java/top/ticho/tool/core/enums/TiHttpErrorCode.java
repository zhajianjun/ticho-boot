package top.ticho.tool.core.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 状态错误码
 *
 * @author zhajianjun
 * @date 2025-10-19 13:07
 */
@Getter
@AllArgsConstructor
@ToString
public enum TiHttpErrorCode implements Serializable, TiErrorCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "错误的请求"),
    NOT_LOGIN(401, "请登录"),
    TOKEN_INVALID(401, "TOKEN 失效"),
    NOT_ACTIVED(402, "用户未激活"),
    ACCESS_DENIED(403, "权限不足"),
    NOT_FOUND(404, "啥都没有"),
    METHOD_NOT_ALLOWED(405, "不允许的方法"),
    NOT_ACCEPTABLE(406, "不可接受"),
    UNSUPPORTED_MEDIA_TYPE(415, "不受支持的媒体类型"),
    FAIL(500, "执行失败"),
    INTERNAL_SERVER_ERROR(500, "系统异常"),
    BAD_GATEWAY(502, "错误的网关"),
    SERVICE_UNAVAILABLE(503, "服务不可用");

    @Serial
    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final int code;
    /** 错误信息 */
    private final String message;

}
