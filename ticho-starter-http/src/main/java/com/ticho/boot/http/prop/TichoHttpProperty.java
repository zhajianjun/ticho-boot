package com.ticho.boot.http.prop;

import lombok.Data;

/**
 * http参数
 *
 * @author zhajianjun
 * @date 2022-11-01 13:12
 */
@Data
public class TichoHttpProperty {

    /** 开启ticho feign，默认使用okhttp */
    private Boolean enable = true;

    /** 开启日志拦截 */
    private Boolean openLog = false;

    /* 日志拦截是否打印日志 */
    private Boolean printLog = true;

    /** 设置连接超时，单位:秒(s) */
    private Long connectTimeout = 10L;

    /** 设置读超时，单位:秒(s) */
    private Long readTimeout = 10L;

    /** 设置写超时，单位:秒(s) */
    private Long writeTimeout = 10L;

    /** 是否自动重连 */
    private Boolean retryOnConnectionFailure = true;

    /** 最大连接数 */
    private int maxIdleConnections = 10;

    /** 存活时间，单位:分钟(min) */
    private Long keepAliveDuration = 5L;

}
