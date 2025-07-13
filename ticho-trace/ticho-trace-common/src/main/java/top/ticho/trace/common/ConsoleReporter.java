package top.ticho.trace.common;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:18
 */
public class ConsoleReporter implements Reporter {
    @Override
    public void report(Span span) {
        System.out.println("Reporting Single Span:");
        printSpan(span);
    }

    @Override
    public void reportBatch(List<Span> spans) {
        System.out.println("Reporting Batch Spans (" + spans.size() + "):");
        for (Span span : spans) {
            printSpan(span);
        }
        System.out.println("-----------------------------");
    }

    private void printSpan(Span span) {
        System.out.println("TraceId: " + span.getTraceId());
        System.out.println("SpanId: " + span.getSpanId());
        System.out.println("ParentSpanId: " + span.getParentSpanId());
        System.out.println("Name: " + span.getName());
        System.out.println("Duration: " + span.getDuration() + "ms");
        System.out.println("Tags: " + span.getTags());
        System.out.println("Logs: " + span.getLogs());
    }

}