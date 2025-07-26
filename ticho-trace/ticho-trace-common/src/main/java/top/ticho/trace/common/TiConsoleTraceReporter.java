package top.ticho.trace.common;

import top.ticho.tool.json.util.TiJsonUtil;

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
    public void report(List<TiSpan> tiSpans) {
        if (Objects.isNull(tiSpans) || tiSpans.isEmpty()) {
            return;
        }
        System.out.println("<Trace>" + TiJsonUtil.toJsonString(tiSpans) + "</Trace>");
    }

}