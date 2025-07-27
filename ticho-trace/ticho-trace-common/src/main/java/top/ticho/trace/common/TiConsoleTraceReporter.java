package top.ticho.trace.common;

import top.ticho.tool.json.util.TiJsonUtil;

import java.util.Collections;
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
        report(Collections.singletonList(tiSpan));
    }

    @Override
    public void report(List<TiSpan> tiSpans) {
        if (Objects.isNull(tiSpans) || tiSpans.isEmpty()) {
            return;
        }
        System.out.println("<Trace>" + TiJsonUtil.toJsonString(tiSpans) + "</Trace>");
    }

}