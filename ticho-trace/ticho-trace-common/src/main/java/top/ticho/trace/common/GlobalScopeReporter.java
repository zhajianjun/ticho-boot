package top.ticho.trace.common;

/**
 * @author zhajianjun
 * @date 2025-07-13 15:01
 */
public class GlobalScopeReporter implements Reporter {
    private final Reporter delegate;
    private final ThreadLocal<Tracer> globalContext = ThreadLocal.withInitial(Tracer::new);

    public GlobalScopeReporter(Reporter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void report(Span span) {
        globalContext.get().addSpan(span);
    }

    /**
     * 请求开始时调用
     */
    public void start() {
        globalContext.get().start();
    }

    /**
     * 请求结束时调用，触发批量上报
     */
    public void end() {
        Tracer context = globalContext.get();
        context.end();
        if (!context.getSpans().isEmpty()) {
            delegate.reportBatch(context.getSpans());
        }
        globalContext.remove();
    }

}