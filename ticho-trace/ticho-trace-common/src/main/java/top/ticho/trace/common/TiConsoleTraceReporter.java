package top.ticho.trace.common;

import java.util.List;
import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:18
 */
public class TiConsoleTraceReporter implements TiTraceReporter {

    @Override
    public void report(TiSpan tiSpan) {
        if (Objects.isNull(tiSpan)) {
            return;
        }
        String sb = "-------------Reporting Single Span:----------------\n" +
            "TraceId: " + tiSpan.getTraceId() + "\n" +
            "SpanId: " + tiSpan.getSpanId() + "\n" +
            "ParentSpanId: " + tiSpan.getParentSpanId() + "\n" +
            "Name: " + tiSpan.getName() + "\n" +
            "Duration: " + tiSpan.getDuration() + "ms\n" +
            "Tags: " + tiSpan.getTags();
        System.out.println(sb);

    }

    @Override
    public void reportBatch(List<TiSpan> tiSpans) {
        if (Objects.isNull(tiSpans) || tiSpans.isEmpty()) {
            return;
        }
        System.out.println("-------------Reporting Batch Spans:----------------");
        for (int i = 0; i < tiSpans.size(); i++) {
            StringBuilder t = new StringBuilder();
            // 根据索引添加缩进
            t.append("\t".repeat(i));
            TiSpan tiSpan = tiSpans.get(i);
            String sb = t + "TraceId: " + tiSpan.getTraceId() + "\n" +
                t + "SpanId: " + tiSpan.getSpanId() + "\n" +
                t + "ParentSpanId: " + tiSpan.getParentSpanId() + "\n" +
                t + "Name: " + tiSpan.getName() + "\n" +
                t + "Duration: " + tiSpan.getDuration() + "ms\n" +
                t + "Tags: " + tiSpan.getTags();
            System.out.println(sb);
        }
    }

}