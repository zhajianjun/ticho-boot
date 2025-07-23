package top.ticho.trace.common;

/**
 * @author zhajianjun
 * @date 2025-07-23 22:46
 */
public class TiTraceRunable implements Runnable {
    private final Runnable runnable;

    public TiTraceRunable(Runnable runnable, TiTraceReporter tiTraceReporter) {
        this.runnable = TiTraceRunableDecorator.decorate(runnable, tiTraceReporter);
    }

    @Override
    public void run() {
        runnable.run();
    }

}
