package top.ticho.trace.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:26
 */
public class Tracer {
    private final Stack<Span> spanStack = new Stack<>();
    private final List<Span> spans = new ArrayList<>();
    private final Reporter reporter;

    public Tracer(Reporter reporter) {
        this.reporter = reporter;
    }

    public Span startSpan(String name) {
        Span span;
        if (spanStack.isEmpty()) {
            // 根Span
            span = new Span(name);
            TraceContext.setTraceId(span.getTraceId());
        } else {
            // 子Span
            Span parentSpan = spanStack.peek();
            span = new Span(name, parentSpan.getTraceId(), parentSpan.getSpanId());
        }
        spanStack.push(span);
        spans.add(span);
        TraceContext.setSpanId(span.getSpanId());
        return span;
    }

    public void finishSpan(Span span) {
        if (!spanStack.isEmpty() && spanStack.peek() == span) {
            span.finish();
            spanStack.pop();
            report(span);
            if (spanStack.isEmpty()) {
                TraceContext.clear();
            } else {
                TraceContext.setSpanId(spanStack.peek().getSpanId());
            }
        }
    }

    public Span currentSpan() {
        return spanStack.isEmpty() ? null : spanStack.peek();
    }

    public List<Span> getSpans() {
        return Collections.unmodifiableList(spans);
    }

    public void clear() {
        spanStack.clear();
        spans.clear();
    }

    public void report(Span span) {
        reporter.report(span);
    }

    public void reportAll() {
        reporter.reportBatch(spans);
    }

}
