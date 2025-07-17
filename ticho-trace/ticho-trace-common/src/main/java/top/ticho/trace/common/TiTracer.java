package top.ticho.trace.common;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:26
 */
@Slf4j
public class TiTracer {
    private TiSpan rootSpan;
    private TiSpan childSpan;
    private TiReporter tiReporter = new TiConsoleReporter();
    private final List<TiSpan> allSpans = new ArrayList<>();


    public void replaceReporter(TiReporter tiReporter) {
        this.tiReporter = tiReporter;
    }

    public void start(TiSpan rootSpan) {
        if (Objects.nonNull(this.rootSpan)) {
            log.warn("链路已开启，已存在根节点链路");
            return;
        }
        this.rootSpan = rootSpan;
        allSpans.add(rootSpan);
    }

    public TiSpan start(String name, String traceId, String spanId, String parentSpanId) {
        if (Objects.nonNull(rootSpan)) {
            log.warn("链路已开启，已存在根链路");
            return null;
        }
        TiSpan rootSpan = new TiSpan(name, traceId, spanId, parentSpanId);
        this.rootSpan = rootSpan;
        this.allSpans.add(rootSpan);
        return rootSpan;
    }

    public TiSpan close() {
        rootSpan.close();
        report(rootSpan);
        reportAll();
        clear();
        return rootSpan;
    }

    public TiSpan startSpan(String name) {
        if (Objects.isNull(rootSpan)) {
            log.warn("开启子链路异常，链路未开启");
            return null;
        }
        if (Objects.nonNull(childSpan)) {
            log.warn("开启子链路异常，请完成上一个子链路");
            return null;
        }
        TiSpan tiSpan = new TiSpan(name, rootSpan.getTraceId(), IdUtil.getSnowflakeNextIdStr(), rootSpan.getSpanId());
        this.childSpan = tiSpan;
        this.allSpans.add(rootSpan);
        return tiSpan;
    }

    public TiSpan closeSpan() {
        if (Objects.isNull(rootSpan)) {
            log.warn("关闭子链路异常，链路未开启");
            return null;
        }
        if (Objects.isNull(childSpan)) {
            log.warn("关闭子链路异常，没有未完成的子链路");
            return null;
        }
        childSpan.close();
        report(childSpan);
        return childSpan;
    }

    public TiSpan rootSpan() {
        return this.rootSpan;
    }

    public TiSpan childSpan() {
        return this.childSpan;
    }

    public List<TiSpan> allSpans() {
        return allSpans;
    }

    public void clear() {
        allSpans.clear();
    }

    public void report(TiSpan tiSpan) {
        tiReporter.report(tiSpan);
    }

    public void reportAll() {
        tiReporter.reportBatch(allSpans);
    }

}
