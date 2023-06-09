package com.ticho.boot.log.interceptor;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.log.event.WebLogEvent;
import com.ticho.boot.log.wrapper.RequestWrapper;
import com.ticho.boot.log.wrapper.ResponseWrapper;
import com.ticho.boot.view.log.BaseLogProperty;
import com.ticho.boot.view.log.HttpLog;
import com.ticho.trace.spring.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志拦截器
 *
 * @author zhajianjun
 * @date 2023-01-11 09:44
 */
@Slf4j
public class WebLogInterceptor implements HandlerInterceptor, Ordered {
    /** NONE */
    private static final String NONE = "NONE";
    /** 用户代理key */
    private static final String USER_AGENT = "User-Agent";
    /** 日志信息线程变量 */
    private static final TransmittableThreadLocal<HttpLog> logTheadLocal = new TransmittableThreadLocal<>();
    /** 日志信息线程变量 */
    private static final TransmittableThreadLocal<Boolean> antPathMatchLocal = new TransmittableThreadLocal<>();
    /** 日志配置 */
    private final BaseLogProperty baseLogProperty;
    /** url地址匹配 */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /** 环境变量 */
    private final Environment environment;

    public WebLogInterceptor(BaseLogProperty baseLogProperty, Environment environment) {
        this.baseLogProperty = baseLogProperty;
        this.environment = environment;
    }

    public static HttpLog logInfo() {
        return logTheadLocal.get();
    }

    // @formatter:off
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        Map<String, Object> paramsMap = null;
        if (request instanceof StandardMultipartHttpServletRequest){
            paramsMap = new HashMap<>();
            StandardMultipartHttpServletRequest sr = (StandardMultipartHttpServletRequest) request;
            request = sr.getRequest();
            MultiValueMap<String,MultipartFile> multiFileMap = sr.getMultiFileMap();
            Map<String,Object> finalParamsMap = paramsMap;
            multiFileMap.forEach((x, y)-> finalParamsMap.put(x, y.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.joining(",", "文件：",""))));
        }
        if (!(request instanceof RequestWrapper) || !(handler instanceof HandlerMethod)) {
            return true;
        }
        long millis = SystemClock.now();
        String type = request.getMethod();
        String url = request.getRequestURI();
        if (paramsMap == null) {
            paramsMap = new HashMap<>();
        }
        // params
        Map<String, Object> paramsMapFromRequest = getParams(request);
        paramsMap.putAll(paramsMapFromRequest);
        String params = toJsonOfDefault(paramsMap);
        // body
        RequestWrapper requestWrapper = (RequestWrapper) request;
        String body = nullOfDefault(requestWrapper.getBody());
        // header
        Map<String, String> headersMap = getHeaders(request);
        String headers = toJsonOfDefault(headersMap);
        String reqPrefix = baseLogProperty.getReqPrefix();
        String header = request.getHeader(USER_AGENT);
        UserAgent userAgent =  UserAgentUtil.parse(header);
        Principal principal = request.getUserPrincipal();
        String port = environment.getProperty("server.port");
        HttpLog httpLog = HttpLog.builder()
            .type(type)
            .ip(IpUtil.getIp(request))
            .url(url)
            .port(port)
            .reqParams(params)
            .reqBody(body)
            .reqHeaders(headers)
            .start(millis)
            .username((principal != null ? principal.getName() : null))
            .userAgent(userAgent)
            .build();
        logTheadLocal.set(httpLog);
        boolean print = Boolean.TRUE.equals(baseLogProperty.getPrint());
        List<String> antPatterns = baseLogProperty.getAntPatterns();
        boolean anyMatch = antPatterns.stream().anyMatch(x -> antPathMatcher.match(x, url));
        antPathMatchLocal.set(anyMatch);
        if (print && !anyMatch) {
            log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", reqPrefix, type, url, params, body, headers);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        HttpLog httpLog = logTheadLocal.get();
        if (httpLog == null) {
            return;
        }
        String type = request.getMethod();
        String url = request.getRequestURI();
        String resBody = nullOfDefault(getResBody(response));
        httpLog.setResBody(resBody);
        String reqPrefix = baseLogProperty.getReqPrefix();
        long end = SystemClock.now();
        httpLog.setEnd(end);
        int status = response.getStatus();
        Long consume = httpLog.getConsume();
        boolean print = Boolean.TRUE.equals(baseLogProperty.getPrint());
        Boolean anyMatch = antPathMatchLocal.get();
        if (print && !anyMatch) {
            log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", reqPrefix, type, url, status, consume, resBody);
        }
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        applicationContext.publishEvent(new WebLogEvent(applicationContext, httpLog));
        logTheadLocal.remove();
        antPathMatchLocal.remove();
    }

    private String getResBody(HttpServletResponse response) {
        String contentType = response.getContentType();
        boolean flag = contentType != null && (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) ||
                contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        if (!flag) {
            return NONE;
        }
        ResponseWrapper responseWrapper = (ResponseWrapper)response;
        return responseWrapper.getBody();
    }

    public Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> parameteNames = request.getParameterNames();
        while (parameteNames.hasMoreElements()) {
            //获得每个文本域的name
            String name = parameteNames.nextElement();
            //根据文本域的name来获取值
            //因为无法判断文本域是否是单值或者双值，所以我们全部使用双值接收
            String[] values = request.getParameterValues(name);
            String value = String.join(",", Arrays.stream(values).filter(StrUtil::isNotBlank).collect(Collectors.joining(",")));
            map.put(name, value);
        }
        return map;
    }

    public Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
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

    public Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String name : headerNames) {
            String value = response.getHeader(name);
            map.put(name, value);
        }
        return map;
    }

    private String toJsonOfDefault(Map<String, ?> map) {
        String result = JsonUtil.toJsonString(map);
        return nullOfDefault(result);
    }

    private String nullOfDefault(String result) {
        if (result == null || result.isEmpty()) {
            return NONE;
        }
        return result;
    }

    @Override
    public int getOrder() {
        return baseLogProperty.getOrder();
    }

}