package com.ticho.boot.view.enums;

/**
 * 错误码接口
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
public interface IErrCode {

    /**
     * 错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 错误信息
     *
     * @return java.lang.String 错误信息
     */
    String getMsg();
}
