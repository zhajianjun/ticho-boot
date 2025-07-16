package top.ticho.trace.common;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:26
 */
@Slf4j
public class TiTracer {
    private final ArrayDeque<TiSpan> tiSpans = new ArrayDeque<>();
    private TiReporter tiReporter = new TiConsoleReporter();


    public void replaceReporter(TiReporter tiReporter) {
        this.tiReporter = tiReporter;
    }

    public void addSpan(TiSpan tiSpan) {
        tiSpans.add(tiSpan);
    }

    public void start(TiSpan rootSpan) {
        if (!tiSpans.isEmpty()) {
            log.warn("链路已开启！已存在根节点Span");
            return;
        }
        addSpan(rootSpan);
    }

    public void start(String name, String traceId, String spanId, String parentSpanId) {
        if (!tiSpans.isEmpty()) {
            log.warn("链路已开启！已存在根节点Span");
            return;
        }
        TiSpan rootSpan = new TiSpan(name, traceId, spanId, parentSpanId);
        addSpan(rootSpan);
    }

    public void finish() {
        reportAll();
        clear();
    }

    public TiSpan startSpan(String name) {
        // 根Span
        TiSpan rootTiSpan = tiSpans.peek();
        if (rootTiSpan == null) {
            return null;
        }
        TiSpan tiSpan = new TiSpan(name, rootTiSpan.getTraceId(), IdUtil.getSnowflakeNextIdStr(), rootTiSpan.getSpanId());
        addSpan(tiSpan);
        return tiSpan;
    }

    public void finishSpan(TiSpan tiSpan) {
        TiSpan lastTiSpan = tiSpans.peekLast();
        if (Objects.equals(lastTiSpan, tiSpan)) {
            tiSpan.finish();
            report(tiSpan);
        }
    }

    public TiSpan currentSpan() {
        return tiSpans.isEmpty() ? null : tiSpans.peekLast();
    }

    public List<TiSpan> getSpans() {
        return new ArrayList<>(tiSpans);
    }

    public void clear() {
        tiSpans.clear();
        TiTraceContext.clear();
    }

    public void report(TiSpan tiSpan) {
        tiReporter.report(tiSpan);
    }

    public void reportAll() {
        tiReporter.reportBatch(new ArrayList<>(tiSpans));
    }

}
