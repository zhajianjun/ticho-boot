package top.ticho.trace.common;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:13
 */
public interface TiReporter {

    void report(TiSpan tiSpan);

    // 批量上报
    default void reportBatch(List<TiSpan> tiSpans) {
        for (TiSpan tiSpan : tiSpans) {
            report(tiSpan);
        }
    }

}
