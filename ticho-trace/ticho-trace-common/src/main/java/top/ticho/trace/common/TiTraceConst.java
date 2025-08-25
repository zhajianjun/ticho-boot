package top.ticho.trace.common;

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
    /** 父跨度id key */
    public static final String PARENT_SPAN_ID_KEY = "parentSpanId";
    /** 第一个跨度id */
    public static final String FIRST_SPAN_ID = "0";
    /** 链路表达式 key */
    public static final String TRACE_KEY = "trace";
    /** 默认链路表达式 */
    public static final String DEFAULT_TRACE = "[${traceId!}].[${parentSpanId!}].[${spanId!}]";

}
