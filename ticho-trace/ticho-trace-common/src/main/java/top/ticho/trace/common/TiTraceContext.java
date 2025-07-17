package top.ticho.trace.common;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;
import top.ticho.trace.common.constant.TiTraceConst;
import top.ticho.trace.common.util.TiBeetlUtil;

import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-07-13 13:22
 */
public class TiTraceContext {
    private static final ThreadLocal<TiTracer> context = new ThreadLocal<>();

    public static void init() {
        context.set(new TiTracer());
    }

    public static void init(TiTracer tiTracer) {
        context.set(tiTracer);
    }

    private static TiTracer getTiTracer() {
        TiTracer tiTracer = context.get();
        Objects.requireNonNull(tiTracer, "TiTracer is null");
        return tiTracer;
    }

    public static TiSpan start(String name) {
        TiTracer tiTracer = getTiTracer();
        String traceId = IdUtil.getSnowflakeNextIdStr();
        MDC.put(TiTraceConst.TRACE_KEY, traceId);
        MDC.put(TiTraceConst.SPAN_ID_KEY, TiTraceConst.FIRST_SPAN_ID);
        MDC.put(TiTraceConst.PARENT_SPAN_ID_KEY, null);
        String trace = MDC.get(TiTraceConst.TRACE_KEY);
        if (trace == null) {
            trace = TiTraceConst.DEFAULT_TRACE;
        }
        MDC.put(TiTraceConst.TRACE_KEY, TiBeetlUtil.render(trace, MDC.getCopyOfContextMap()));
        return tiTracer.start(name, traceId, TiTraceConst.FIRST_SPAN_ID, null);
    }

    public static TiSpan start(String name, String traceId, String parentSpanId) {
        TiTracer tiTracer = getTiTracer();
        String spanId = IdUtil.getSnowflakeNextIdStr();
        MDC.put(TiTraceConst.TRACE_KEY, traceId);
        MDC.put(TiTraceConst.SPAN_ID_KEY, spanId);
        MDC.put(TiTraceConst.PARENT_SPAN_ID_KEY, parentSpanId);
        String trace = MDC.get(TiTraceConst.TRACE_KEY);
        if (trace == null) {
            trace = TiTraceConst.DEFAULT_TRACE;
        }
        MDC.put(TiTraceConst.TRACE_KEY, TiBeetlUtil.render(trace, MDC.getCopyOfContextMap()));
        return tiTracer.start(name, traceId, spanId, parentSpanId);
    }

    public static void start(TiSpan rootSpan) {
        getTiTracer().start(rootSpan);
    }

    public static TiSpan startSpan(String name) {
        return getTiTracer().startSpan(name);
    }

    public static TiSpan closeSpan() {
        return getTiTracer().closeSpan();
    }

    public static TiSpan finish() {
        return getTiTracer().close();
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
