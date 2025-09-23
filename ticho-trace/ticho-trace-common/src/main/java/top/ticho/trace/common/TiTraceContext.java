package top.ticho.trace.common;

import org.slf4j.MDC;
import top.ticho.tool.core.TiIdUtil;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.template.TiTemplateUtil;

import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-07-13 13:22
 */
public class TiTraceContext {
    private static final ThreadLocal<TiTracer> context = new ThreadLocal<>();

    public static void init(TiTraceReporter tiTraceReporter) {
        context.set(new TiTracer(tiTraceReporter));
    }

    public static void init(TiTracer tiTracer) {
        context.set(tiTracer);
    }

    public static TiTracer getTiTracer() {
        return context.get();
    }

    public static TiSpan start(String name, String trace) {
        TiTracer tiTracer = getTiTracer();
        String traceId = TiIdUtil.simpleUuid();
        MDC.put(TiTraceConst.TRACE_ID_KEY, traceId);
        MDC.put(TiTraceConst.SPAN_ID_KEY, TiTraceConst.FIRST_SPAN_ID);
        MDC.put(TiTraceConst.PARENT_SPAN_ID_KEY, null);
        if (trace == null) {
            trace = TiTraceConst.DEFAULT_TRACE;
        }
        renderTrace(trace, MDC.getCopyOfContextMap());
        return tiTracer.start(name, traceId, TiTraceConst.FIRST_SPAN_ID, null);
    }

    public static TiSpan start(String name, String traceId, String parentSpanId, String trace) {
        TiTracer tiTracer = getTiTracer();
        if (TiStrUtil.isBlank(traceId)) {
            traceId = TiIdUtil.simpleUuid();
            parentSpanId = TiTraceConst.FIRST_SPAN_ID;
        }
        String spanId = TiIdUtil.shortUuid();
        MDC.put(TiTraceConst.TRACE_ID_KEY, traceId);
        MDC.put(TiTraceConst.SPAN_ID_KEY, spanId);
        MDC.put(TiTraceConst.PARENT_SPAN_ID_KEY, parentSpanId);
        if (trace == null) {
            trace = TiTraceConst.DEFAULT_TRACE;
        }
        MDC.put(TiTraceConst.TRACE_KEY, TiTemplateUtil.render(trace, MDC.getCopyOfContextMap()));
        return tiTracer.start(name, traceId, spanId, parentSpanId);
    }

    public static void start(TiSpan rootSpan) {
        getTiTracer().start(rootSpan);
    }

    public static void addTag(TiTraceTag tiTraceTag, String value) {
        getTiTracer().rootSpan().addTag(tiTraceTag, value);
    }

    public static String getTag(TiTraceTag tiTraceTag) {
        return getTiTracer().rootSpan().getTag(tiTraceTag);
    }

    public static TiSpan close() {
        return getTiTracer().end();
    }

    public static TiSpan close(boolean report) {
        return getTiTracer().end(report);
    }

    public static TiSpan startSpan(String name) {
        return getTiTracer().startSpan(name);
    }

    public static void addSpanTag(TiTraceTag tiTraceTag, String value) {
        getTiTracer().childSpan().addTag(tiTraceTag, value);
    }

    public static TiSpan closeSpan() {
        return getTiTracer().endSpan();
    }

    public static void clear() {
        MDC.clear();
    }

    public static String getTraceId() {
        return MDC.get(TiTraceConst.TRACE_ID_KEY);
    }

    public static String getSpanId() {
        return MDC.get(TiTraceConst.SPAN_ID_KEY);
    }

    public static void setTraceId(String traceId) {
        MDC.put(TiTraceConst.TRACE_ID_KEY, traceId);
    }

    public static void setSpanId(String spanId) {
        MDC.put(TiTraceConst.SPAN_ID_KEY, spanId);
    }

    public static String getTrace() {
        return MDC.get(TiTraceConst.TRACE_KEY);
    }

    public static void renderTrace(String trace, String key, String value) {
        String render = TiTemplateUtil.render(trace, key, value);
        MDC.put(TiTraceConst.TRACE_KEY, render);
    }

    public static void renderTrace(String trace, Map<String, String> traceMap) {
        String render = TiTemplateUtil.render(trace, traceMap);
        MDC.put(TiTraceConst.TRACE_KEY, render);
    }

}
