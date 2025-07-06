package top.ticho.trace.common.constant;

import lombok.NoArgsConstructor;

/**
 * 日志静态常量
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TiTraceConst {

    /** 链路id key */
    public static final String TRACE_ID_KEY = "traceId";
    /** 跨度id key */
    public static final String SPAN_ID_KEY = "spanId";
    /** 第一个跨度id */
    public static final String FIRST_SPAN_ID = "0";
    /** 当前应用名称 key */
    public static final String APP_NAME_KEY = "appName";
    /** 当前ip key */
    public static final String IP_KEY = "ip";
    /** 上个链路的应用名称 key */
    public static final String PRE_APP_NAME_KEY = "preAppName";
    /** 上个链路的Ip key */
    public static final String PRE_IP_KEY = "preIp";
    /** UNKNOWN */
    public static final String UNKNOWN = "UNKNOWN";
    /** 链路表达式 key */
    public static final String TRACE_KEY = "trace";
    /** 默认链路表达式 */
    public static final String DEFAULT_TRACE = "[${traceId!}].[${spanId!}]";

}
