package top.ticho.trace.common;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;
import top.ticho.trace.common.constant.TiTraceConst;

/**
 * @author zhajianjun
 * @date 2025-07-13 13:22
 */
public class TiTraceContext {
    private static final ThreadLocal<TiTracer> context = ThreadLocal.withInitial(TiTracer::new);

    public void start(String name) {
        start(name, IdUtil.getSnowflakeNextIdStr(), TiTraceConst.FIRST_SPAN_ID, null);
    }

    public void start(String name, String traceId, String spanId, String parentSpanId) {
        MDC.put(TiTraceConst.TRACE_KEY, traceId);
        MDC.put(TiTraceConst.SPAN_ID_KEY, spanId);
        context.get().start(name, traceId, spanId, parentSpanId);
    }

    public static void clear() {
        MDC.clear();
    }

    public static String getTraceId() {
        return MDC.get(TiTraceConst.TRACE_KEY);
    }

    public static String getSpanId() {
        return MDC.get(TiTraceConst.SPAN_ID_KEY);
    }

    public static void setTraceId(String traceId) {
        MDC.put(TiTraceConst.TRACE_KEY, traceId);
    }

    public static void setSpanId(String spanId) {
        MDC.put(TiTraceConst.SPAN_ID_KEY, spanId);
    }

}
