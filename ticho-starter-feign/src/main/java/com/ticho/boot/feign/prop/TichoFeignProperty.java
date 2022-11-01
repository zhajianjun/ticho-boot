package com.ticho.boot.feign.prop;

import lombok.Data;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 13:12
 */
@Data
public class TichoFeignProperty {

    /** 开启ticho feign，默认使用okhttp */
    private Boolean enable = true;

    private Boolean openLog = false;

    private Level level = Level.DEBUG;

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

    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

}
