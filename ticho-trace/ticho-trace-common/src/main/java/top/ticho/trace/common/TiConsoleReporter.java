package top.ticho.trace.common;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:18
 */
public class TiConsoleReporter implements TiReporter {
    @Override
    public void report(TiSpan tiSpan) {
        System.out.println("Reporting Single Span:");
        printSpan(tiSpan);
    }

    @Override
    public void reportBatch(List<TiSpan> tiSpans) {
        System.out.println("Reporting Batch Spans (" + tiSpans.size() + "):");
        for (TiSpan tiSpan : tiSpans) {
            printSpan(tiSpan);
        }
        System.out.println("-----------------------------");
    }

    private void printSpan(TiSpan tiSpan) {
        System.out.println("TraceId: " + tiSpan.getTraceId());
        System.out.println("SpanId: " + tiSpan.getSpanId());
        System.out.println("ParentSpanId: " + tiSpan.getParentSpanId());
        System.out.println("Name: " + tiSpan.getName());
        System.out.println("Duration: " + tiSpan.getDuration() + "ms");
        System.out.println("Tags: " + tiSpan.getTags());
    }

}