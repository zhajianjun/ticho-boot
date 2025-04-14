package top.ticho.starter.view.core;

import lombok.Data;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.enums.TiErrCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应消息
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Data
public class TiResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务码 */
    private int code;
    /** 业务信息 */
    private String msg;
    /** 业务数据 */
    private T data;
    /** 时间戳 */
    private long time;

    public TiResult() {
        TiBizErrCode success = TiBizErrCode.SUCCESS;
        this.code = success.getCode();
        this.msg = success.getMsg();
        this.time = System.currentTimeMillis();
    }

    public TiResult(TiErrCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.time = System.currentTimeMillis();
    }

    public TiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.time = System.currentTimeMillis();
    }

    public TiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public static <T> TiResult<T> of(int code, String msg) {
        return new TiResult<>(code, msg);
    }

    public static <T> TiResult<T> of(int code, String msg, T data) {
        return new TiResult<>(code, msg, data);
    }

    public static <T> TiResult<T> of(TiErrCode resultCode) {
        return new TiResult<>(resultCode.getCode(), resultCode.getMsg());
    }

    public static <T> TiResult<T> of(TiErrCode resultCode, T data) {
        return new TiResult<>(resultCode.getCode(), resultCode.getMsg(), data);
    }

    public static <T> TiResult<T> of(TiErrCode resultCode, String msg, T data) {
        return new TiResult<>(resultCode.getCode(), msg, data);
    }

    public static <T> TiResult<T> ok() {
        return new TiResult<>();
    }

    public static <T> TiResult<T> ok(T data) {
        return new TiResult<>(TiBizErrCode.SUCCESS.getCode(), TiBizErrCode.SUCCESS.getMsg(), data);
    }

    public static <T> TiResult<T> ok(String msg, T data) {
        return new TiResult<>(TiBizErrCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> TiResult<T> fail() {
        return of(TiBizErrCode.FAIL);
    }

    public static <T> TiResult<T> fail(String msg) {
        return of(TiBizErrCode.FAIL.getCode(), msg, null);
    }

    public static <T> TiResult<T> fail(TiErrCode errCode, String msg) {
        return of(errCode, msg, null);
    }

    public static <T> TiResult<T> condition(boolean flag) {
        return flag ? ok() : fail();
    }

    public TiResult<T> code(int code) {
        this.code = code;
        return this;
    }

    public TiResult<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public TiResult<T> data(T data) {
        this.data = data;
        return this;
    }
}
