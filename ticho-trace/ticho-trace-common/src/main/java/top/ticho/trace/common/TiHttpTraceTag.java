package top.ticho.trace.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhajianjun
 * @date 2025-07-22 22:48
 */
@Getter
@AllArgsConstructor
public enum TiHttpTraceTag implements TiTraceTag {

    IP("http.ip"),
    ENV("http.env"),
    URL("http.url"),
    METHOD("http.method"),
    TYPE("http.type"),
    STATUS("http.status"),
    ASYNC("thread.async"),
    ;

    private final String key;

}
