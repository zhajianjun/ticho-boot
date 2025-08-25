package top.ticho.trace.common;

/**
 * @author zhajianjun
 * @date 2025-07-23 22:55
 */
public class TiTraceRunableDecorator {

    public static Runnable decorate(Runnable runnable, TiTraceReporter tiTraceReporter) {
        TiTracer tiTracer = TiTraceContext.getTiTracer();
        return () -> {
            try {
                execute(tiTracer, tiTraceReporter);
                runnable.run();
            } finally {
                complete(tiTracer);
            }
        };
    }

    public static void complete(TiTracer tiTracer) {
        if (tiTracer == null) {
            return;
        }
        TiTraceContext.close();
    }

    public static void execute(TiTracer tiTracer, TiTraceReporter tiTraceReporter) {
        if (tiTracer == null) {
            return;
        }
        TiTraceContext.init(tiTraceReporter);
        TiSpan newRootSpan = tiTracer.rootSpan().copy();
        newRootSpan.addTag(TiHttpTraceTag.ASYNC, newRootSpan.getTraceId());
        TiTraceContext.start(newRootSpan);
    }

}
