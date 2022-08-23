package com.ticho.boot.view.core;


import com.ticho.boot.view.enums.IErrCode;

import java.io.Serializable;

/**
 * 通用业务错误码
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
public enum BizErrCode implements Serializable, IErrCode {

    // @formatter:off

    /**
     *
     */
    SUCCESS(0, "执行成功"),

    FAIL(-1, "执行失败"),

    PARAM_ERROR(1000, "参数不能为空"),

    IS_NOT_EXISTS(1001, "数据不存在"),
    APP_SERVICE_ERR(1001, "数据不存在"),
    ;

    private static final long serialVersionUID = 1L;
    /**
     * 状态码
     */
    private final int code;
    /**
     *状态信息
     */
    private final String msg;

    BizErrCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }



    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        return String.format(" %s:{code=%s, msg=%s} ", this.getClass().getSimpleName(), this.code, this.msg);
    }
}
