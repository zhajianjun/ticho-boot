package top.ticho.trace.common;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:13
 */
public interface TiTraceReporter {

    void report(TiSpan tiSpan);

    void report(List<TiSpan> tiSpans);

}
