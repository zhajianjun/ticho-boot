package com.ticho.boot.log.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.log.prop.TichoLogProperty;
import com.ticho.boot.log.wrapper.RequestWrapper;
import com.ticho.boot.log.wrapper.ResponseWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-01-11 09:44
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class WebLogInterceptor implements HandlerInterceptor, InitializingBean {

    private static final String NONE = "NONE";

    private static TransmittableThreadLocal<LogInfo> logThreadLocal;

    private final TransmittableThreadLocal<LogInfo> theadLocal;

    private final TichoLogProperty tichoLogProperty;

    public WebLogInterceptor(TichoLogProperty tichoLogProperty) {
        this.theadLocal = new TransmittableThreadLocal<>();
        this.tichoLogProperty = tichoLogProperty;
    }

    @Override
    public void afterPropertiesSet() {
        logThreadLocal = this.theadLocal;
    }

    public LogInfo getLogInfo() {
        return theadLocal.get();
    }

    public static LogInfo logInfo() {
        return logThreadLocal.get();
    }

    // @formatter:off

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 是否打印日志
        boolean print = Boolean.TRUE.equals(tichoLogProperty.getPrint());
        if (!print || !(request instanceof RequestWrapper) || !(handler instanceof HandlerMethod)) {
            return true;
        }
        long millis = System.currentTimeMillis();
        String requestPrefixText = tichoLogProperty.getRequestPrefixText();
        log.info("{} 请求日志 开始", requestPrefixText);
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        // params
        Map<String, Object> paramsMap = getParams(request);
        String params = toJsonOfDefault(paramsMap);
        // body
        RequestWrapper requestWrapper = (RequestWrapper) request;
        String body = nullOfDefault(requestWrapper.getBody());
        Map<String,Object> bodyMap = requestWrapper.getBodyMap();
        // header
        Map<String, Object> headersMap = getHeaders(request);
        String headers = toJsonOfDefault(headersMap);
        log.info("{} 请求地址: {} {}", requestPrefixText,  method, requestURI);
        log.info("{} 请求参数: {}", requestPrefixText, params);
        log.info("{} 请求体: {}", requestPrefixText, body);
        log.info("{} 请求头: {}", requestPrefixText, headers);
        LogInfo logInfo = LogInfo.builder()
            .type(method)
            .url(requestURI)
            .reqParams(paramsMap)
            .reqBody(bodyMap)
            .headers(headersMap)
            .start(millis)
            .build();
        theadLocal.set(logInfo);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        LogInfo logInfo = theadLocal.get();
        if (logInfo == null) {
            return;
        }
        Map<String, Object> resBodyMap = getResBody(request, response);
        logInfo.setResBody(resBodyMap);
        String resBody = toJsonOfDefault(resBodyMap);
        String requestPrefixText = tichoLogProperty.getRequestPrefixText();
        log.info("{} 响应体: {}", requestPrefixText, resBody);
        theadLocal.remove();
        logInfo.setEnd(System.currentTimeMillis());
        log.info("{} 耗时: {}ms", requestPrefixText, logInfo.getTime());
        log.info("{} 日志结束", requestPrefixText);
        // 如果是mapping
    }

    private Map<String, Object> getResBody(HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();
        boolean flag = contentType != null && (contentType.equals(MediaType.APPLICATION_JSON_VALUE) ||
            contentType.equals(MediaType.APPLICATION_JSON_UTF8_VALUE) ||
            contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        if (!flag) {
            return Collections.emptyMap();
        }
        ResponseWrapper responseWrapper = (ResponseWrapper)response;
        return responseWrapper.getBodyMap();
    }

    public Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> parameteNames = request.getParameterNames();
        while (parameteNames.hasMoreElements()) {
            //获得每个文本域的name
            String name = parameteNames.nextElement();
            //根据文本域的name来获取值
            //因为无法判断文本域是否是单值或者双值，所以我们全部使用双值接收
            String[] parameteValues = request.getParameterValues(name);
            String value = String.join(",", parameteValues);
            map.put(name, value);
        }
        return map;
    }

    public Map<String, Object> getHeaders(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //获得每个文本域的name
            String name = headerNames.nextElement();
            //根据文本域的name来获取值
            //因为无法判断文本域是否是单值或者双值，所以我们全部使用双值接收
            String value = request.getHeader(name);
            map.put(name, value);
        }
        return map;
    }

    private String toJsonOfDefault(Map<String, Object> map) {
        String result = JsonUtil.toJsonString(map);
        return nullOfDefault(result);
    }

    private String nullOfDefault(String result) {
        if (result == null || result.isEmpty()) {
            return NONE;
        }
        return result;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class LogInfo {
        /** 请求类型 */
        private String type;

        /** 请求地址 */
        private String url;

        /** 请求参数 */
        private Map<String, Object> reqParams;

        /** 请求体 */
        private Map<String, Object> reqBody;

        /** 请求头 */
        private Map<String, Object> headers;

        /** 响应体 */
        private Map<String, Object> resBody;

        /* 请求开始时间 */
        private Long start;

        /* 请求结束时间 */
        private Long end;

        /* 请求间隔 */
        private Long time;

        /* 额外的信息 */
        private Map<String, Object> extra = new HashMap<>();

        public Long getTime() {
            if (start == null || end == null) {
                return -1L;
            }
            return end - start;
        }
    }

}
