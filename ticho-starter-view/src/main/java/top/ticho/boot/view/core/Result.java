package top.ticho.boot.view.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.enums.IErrCode;

/**
 * 统一响应消息
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Data
@ApiModel(value = "统一响应消息")
public class Result<T> {

    @ApiModelProperty(value = "状态码", required = true, position = 10)
    private int code;

    @ApiModelProperty(value = "消息内容", required = true, position = 20)
    private String msg;

    @ApiModelProperty(value = "业务数据", required = true, position = 30)
    private T data;

    @ApiModelProperty(value = "时间戳", required = true, position = 40)
    private long time;

    public Result() {
        BizErrCode success = BizErrCode.SUCCESS;
        this.code = success.getCode();
        this.msg = success.getMsg();
        this.time = System.currentTimeMillis();
    }

    public Result(IErrCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.time = System.currentTimeMillis();
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.time = System.currentTimeMillis();
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public static <T> Result<T> of(int code, String msg) {
        return new Result<>(code, msg);
    }

    public static <T> Result<T> of(int code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static <T> Result<T> of(IErrCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg());
    }

    public static <T> Result<T> of(IErrCode resultCode, T data) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), data);
    }

    public static <T> Result<T> of(IErrCode resultCode, String msg, T data) {
        return new Result<>(resultCode.getCode(), msg, data);
    }

    public static <T> Result<T> ok() {
        return new Result<>();
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(BizErrCode.SUCCESS.getCode(), BizErrCode.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(BizErrCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> Result<T> fail() {
        return of(BizErrCode.FAIL);
    }

    public static <T> Result<T> fail(String msg) {
        return of(BizErrCode.FAIL.getCode(), msg, null);
    }

    public static <T> Result<T> condition(boolean flag) {
        return flag ? ok() : fail();
    }

    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public Result<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}
