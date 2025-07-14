package top.ticho.trace.common;

import cn.hutool.core.util.IdUtil;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-07-13 14:05
 */
@Getter
public class Span {
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
    private final Map<String, String> tags = new HashMap<>();
    /** 日志 */
    private final Map<String, Object> logs = new HashMap<>();

    public Span(String name, String traceId, String spanId, String parentSpanId) {
        this.name = name;
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.startTime = System.currentTimeMillis();
    }

    public void finish() {
        this.endTime = System.currentTimeMillis();
    }

    public void addTag(String key, String value) {
        tags.put(key, value);
    }

    public void addLog(String key, Object value) {
        logs.put(key, value);
    }

    public long getDuration() {
        return endTime - startTime;
    }

}
