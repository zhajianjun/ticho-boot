package top.ticho.trace.common;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:26
 */
public class Tracer {
    private final ArrayDeque<Span> spans = new ArrayDeque<>();
    private final Reporter reporter;

    public Tracer(Reporter reporter) {
        this.reporter = reporter;
    }

    public void start(String name, String traceId, String parentSpanId) {
        Span span;
        if (StrUtil.isBlank(traceId)) {
            TraceContext.setTraceId(IdUtil.getSnowflakeNextIdStr());
            TraceContext.setSpanId(IdUtil.getSnowflakeNextIdStr());
            span = new Span(name, TraceContext.getTraceId(), TraceContext.getSpanId(), null);
        } else {
            String spanId = IdUtil.getSnowflakeNextIdStr();
            TraceContext.setTraceId(traceId);
            TraceContext.setSpanId(spanId);
            span = new Span(name, traceId, spanId, parentSpanId);
        }
        spans.add(span);
    }

    public Span startSpan(String name) {
        // 根Span
        Span rootSpan = spans.peek();
        if (rootSpan == null) {
            if (StrUtil.isBlank(TraceContext.getTraceId())) {
                TraceContext.setTraceId(IdUtil.getSnowflakeNextIdStr());
                TraceContext.setSpanId(IdUtil.getSnowflakeNextIdStr());
            }
            Span span = new Span(name, TraceContext.getTraceId(), TraceContext.getSpanId(), null);
            spans.add(span);
            return span;
        }
        Span span = new Span(name, rootSpan.getTraceId(), IdUtil.getSnowflakeNextIdStr(), rootSpan.getSpanId());
        spans.add(span);
        return span;
    }

    public void finishSpan(Span span) {
        Span lastSpan = spans.peekLast();
        if (Objects.equals(lastSpan, span)) {
            span.finish();
            reporter.report(span);
        }
    }

    public Span currentSpan() {
        return spans.isEmpty() ? null : spans.peekLast();
    }

    public List<Span> getSpans() {
        return new ArrayList<>(spans);
    }

    public void clear() {
        spans.clear();
    }

    public void report(Span span) {
        reporter.report(span);
    }

    public void reportAll() {
        reporter.reportBatch(new ArrayList<>(spans));
    }

}
