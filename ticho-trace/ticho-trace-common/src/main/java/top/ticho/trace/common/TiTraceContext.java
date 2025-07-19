package top.ticho.trace.common;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-07-13 13:22
 */
public class TiTraceContext {
    private static final ThreadLocal<TiTracer> context = new ThreadLocal<>();

    public static void init(TiReporter tiReporter) {
        context.set(new TiTracer(tiReporter));
    }

    public static void init(TiTracer tiTracer) {
        context.set(tiTracer);
    }

    public static TiTracer getTiTracer() {
        TiTracer tiTracer = context.get();
        Objects.requireNonNull(tiTracer, "TiTracer is null");
        return tiTracer;
    }

    public static TiSpan start(String name, String trace) {
        TiTracer tiTracer = getTiTracer();
        String traceId = IdUtil.getSnowflakeNextIdStr();
        MDC.put(TiTraceConst.TRACE_KEY, traceId);
        MDC.put(TiTraceConst.SPAN_ID_KEY, TiTraceConst.FIRST_SPAN_ID);
        MDC.put(TiTraceConst.PARENT_SPAN_ID_KEY, null);
        if (trace == null) {
            trace = TiTraceConst.DEFAULT_TRACE;
        }
        MDC.put(TiTraceConst.TRACE_KEY, TiBeetlUtil.render(trace, MDC.getCopyOfContextMap()));
        return tiTracer.start(name, traceId, TiTraceConst.FIRST_SPAN_ID, null);
    }

    public static TiSpan start(String name, String traceId, String parentSpanId, String trace) {
        TiTracer tiTracer = getTiTracer();
        if (StrUtil.isBlank(traceId)) {
            traceId = IdUtil.getSnowflakeNextIdStr();
            parentSpanId = TiTraceConst.FIRST_SPAN_ID;
        }
        String spanId = IdUtil.getSnowflakeNextIdStr();
        MDC.put(TiTraceConst.TRACE_KEY, traceId);
        MDC.put(TiTraceConst.SPAN_ID_KEY, spanId);
        MDC.put(TiTraceConst.PARENT_SPAN_ID_KEY, parentSpanId);
        if (trace == null) {
            trace = TiTraceConst.DEFAULT_TRACE;
        }
        MDC.put(TiTraceConst.TRACE_KEY, TiBeetlUtil.render(trace, MDC.getCopyOfContextMap()));
        return tiTracer.start(name, traceId, spanId, parentSpanId);
    }

    public static void start(TiSpan rootSpan) {
        getTiTracer().start(rootSpan);
    }

    public static void addTag(String key, String value) {
        getTiTracer().rootSpan().addTag(key, value);
    }

    public static TiSpan close() {
        return getTiTracer().end();
    }

    public static TiSpan startSpan(String name) {
        return getTiTracer().startSpan(name);
    }

    public static void addSpanTag(String key, String value) {
        getTiTracer().childSpan().addTag(key, value);
    }

    public static TiSpan closeSpan() {
        return getTiTracer().endSpan();
    }

    public static TiSpan finish() {
        return getTiTracer().end();
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
