package top.ticho.trace.common;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2025-07-13 13:22
 */
public class TraceContext {
    private static final ThreadLocal<Map<String, String>> context = ThreadLocal.withInitial(HashMap::new);

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    public static String get(String key) {
        return MDC.get(key);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

    public static void clear() {
        MDC.clear();
    }

    public static String getTraceId() {
        return get("traceId");
    }

    public static String getSpanId() {
        return get("spanId");
    }

    public static void setTraceId(String traceId) {
        put("traceId", traceId);
    }

    public static void setSpanId(String spanId) {
        put("spanId", spanId);
    }

}
