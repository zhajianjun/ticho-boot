package com.ticho.boot.log.interceptor;

import cn.hutool.http.useragent.UserAgent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.method.HandlerMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口日志
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LogInfo {
    /** 请求类型 */
    private String type;

    /** 请求地址 */
    private String url;

    /** 请求ip地址 */
    private String ip;

    /** 请求参数 */
    private String reqParams;

    /** 请求体 */
    private String reqBody;

    /** 请求头 */
    private String reqHeaders;

    /** 响应体 */
    private String resBody;

    /** 响应头 */
    private String resHeaders;

    /* 请求开始时间 */
    private Long start;

    /* 请求结束时间 */
    private Long end;

    /* 请求间隔 */
    private Long time;

    /* 额外的信息 */
    private Map<String, Object> extra = new HashMap<>();

    /* User-Agent信息对象 */
    @JsonIgnore
    private UserAgent userAgent;

    @JsonIgnore
    private HandlerMethod handlerMethod;

    public Long getTime() {
        if (start == null || end == null) {
            return -1L;
        }
        return end - start;
    }
}
