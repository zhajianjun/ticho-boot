package top.ticho.starter.view.core;

import lombok.Data;
import top.ticho.tool.core.enums.TiBizErrorCode;
import top.ticho.tool.core.enums.TiErrorCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应消息
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Data
public class TiResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务码 */
    private int code;
    /** 业务信息 */
    private String message;
    /** 业务数据 */
    private T data;
    /** 时间戳 */
    private long time;

    public TiResult() {
        TiBizErrorCode success = TiBizErrorCode.SUCCESS;
        this.code = success.getCode();
        this.message = success.getMessage();
        this.time = System.currentTimeMillis();
    }

    public TiResult(TiErrorCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.time = System.currentTimeMillis();
    }

    public TiResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public TiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public static <T> TiResult<T> of(int code, String message) {
        return new TiResult<>(code, message);
    }

    public static <T> TiResult<T> of(int code, String message, T data) {
        return new TiResult<>(code, message, data);
    }

    public static <T> TiResult<T> of(TiErrorCode resultCode) {
        return new TiResult<>(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> TiResult<T> of(TiErrorCode resultCode, T data) {
        return new TiResult<>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> TiResult<T> of(TiErrorCode resultCode, String message, T data) {
        return new TiResult<>(resultCode.getCode(), message, data);
    }

    public static <T> TiResult<T> ok() {
        return new TiResult<>();
    }

    public static <T> TiResult<T> ok(T data) {
        return new TiResult<>(TiBizErrorCode.SUCCESS.getCode(), TiBizErrorCode.SUCCESS.getMessage(), data);
    }

    public static <T> TiResult<T> ok(String message, T data) {
        return new TiResult<>(TiBizErrorCode.SUCCESS.getCode(), message, data);
    }

    public static <T> TiResult<T> fail() {
        return of(TiBizErrorCode.FAIL);
    }

    public static <T> TiResult<T> fail(String message) {
        return of(TiBizErrorCode.FAIL.getCode(), message, null);
    }

    public static <T> TiResult<T> fail(TiErrorCode errCode, String message) {
        return of(errCode, message, null);
    }

    public static <T> TiResult<T> condition(boolean flag) {
        return flag ? ok() : fail();
    }

    public TiResult<T> code(int code) {
        this.code = code;
        return this;
    }

    public TiResult<T> message(String message) {
        this.message = message;
        return this;
    }

    public TiResult<T> data(T data) {
        this.data = data;
        return this;
    }
}
