package top.ticho.trace.common;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:13
 */
public interface Reporter {

    void report(Span span);

    // 批量上报
    default void reportBatch(List<Span> spans) {
        for (Span span : spans) {
            report(span);
        }
    }

}
