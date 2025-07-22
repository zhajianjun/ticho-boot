package top.ticho.trace.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:05
 */
@Getter
@Setter
@AllArgsConstructor
public class TiSpan {

    /** 操作名称 */
    private final String name;
    /** 全局唯一Trace ID */
    private final String traceId;
    /** 当前Span ID */
    private final String spanId;
    /** 父 Span ID */
    private final String parentSpanId;
    /** 开始时间(毫秒) */
    private long startTime;
    /** 结束时间(毫秒) */
    private long endTime;
    /** 标签 */
    private final Map<String, String> tags;

    public TiSpan(String name, String traceId, String spanId, String parentSpanId) {
        this.name = name;
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.tags = new HashMap<>();
    }

    @JsonAnyGetter
    @JsonAnySetter
    public Map<String, String> getTags() {
        return tags;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
    }

    public void addTag(TiTraceTag tiTraceTag, String value) {
        tags.put(tiTraceTag.getKey(), value);
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public TiSpan copy() {
        return new TiSpan(name, traceId, spanId, parentSpanId, startTime, endTime, new HashMap<>(tags));
    }

    public TiSpan next(String name, String spanId) {
        return new TiSpan(name, traceId, spanId, parentSpanId);
    }

}
