package top.ticho.trace.common;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-07-23 22:55
 */
public class TiTraceRunableDecorator {

    public static Runnable decorate(Runnable runnable, TiTraceReporter ifAvailable) {
        try {
            TiTracer tiTracer = TiTraceContext.getTiTracer();
            return () -> {
                try {
                    if (tiTracer != null) {
                        TiTraceReporter tiTraceReporter = new TiTraceReporter() {
                            @Override
                            public void report(TiSpan tiSpan) {

                            }
                            @Override
                            public void reportBatch(List<TiSpan> tiSpans) {
                                if (ifAvailable == null) {
                                    return;
                                }
                                if (tiSpans == null || tiSpans.isEmpty()) {
                                    return;
                                }
                                tiSpans.remove(0);
                            }
                        };
                        TiTraceContext.init(tiTraceReporter);
                        TiTraceContext.start(tiTracer.rootSpan().copy());
                    }
                    runnable.run();
                } finally {
                    if (tiTracer != null) {
                        TiTraceContext.close();
                    }
                }
            };
        } catch (Exception e) {
            return runnable;
        }
    }

}
