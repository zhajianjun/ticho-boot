package top.ticho.trace.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:05
 */
@Getter
public class TiSpan {
    /** 全局唯一Trace ID */
    private final String traceId;
    /** 当前Span ID */
    private final String spanId;
    /** 父 Span ID */
    private final String parentSpanId;
    /** 操作名称 */
    private final String name;
    /** 开始时间(毫秒) */
    private final long startTime;
    /** 结束时间(毫秒) */
    private long endTime;
    /** 标签 */
    @JsonAnyGetter
    @JsonAnySetter
    private final Map<String, String> tags = new HashMap<>();

    public TiSpan(String name, String traceId, String spanId, String parentSpanId) {
        this.name = name;
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.startTime = System.currentTimeMillis();
    }

    public void close() {
        this.endTime = System.currentTimeMillis();
    }

    public void addTag(String key, String value) {
        tags.put(key, value);
    }

    public long getDuration() {
        return endTime - startTime;
    }

}
