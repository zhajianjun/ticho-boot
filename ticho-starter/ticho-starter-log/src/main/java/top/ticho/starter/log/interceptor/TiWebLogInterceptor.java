package top.ticho.starter.log.interceptor;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ticho.starter.log.annotation.TiLog;
import top.ticho.starter.log.event.TiWebLogEvent;
import top.ticho.starter.log.wrapper.TiRequestWrapper;
import top.ticho.starter.log.wrapper.TiResponseWrapper;
import top.ticho.starter.view.log.TiHttpLog;
import top.ticho.starter.view.log.TiLogProperty;
import top.ticho.tool.json.util.TiJsonUtil;
import top.ticho.trace.spring.util.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 日志拦截器
 *
 * @author zhajianjun
 * @date 2023-01-11 09:44
 */
@Slf4j
public class TiWebLogInterceptor implements HandlerInterceptor, Ordered {

    /** 用户代理key */
    private static final String USER_AGENT = "User-Agent";
    /** 日志信息线程变量 */
    private static final ThreadLocal<TiHttpLog> logTheadLocal = new ThreadLocal<>();
    /** 日志过滤地址匹配线程变量 */
    private static final ThreadLocal<Boolean> antPathMatchLocal = new ThreadLocal<>();
    /** 日志配置 */
    private final TiLogProperty tiLogProperty;
    /** url地址匹配 */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /** 环境变量 */
    private final Environment environment;

    public TiWebLogInterceptor(TiLogProperty tiLogProperty, Environment environment) {
        this.tiLogProperty = tiLogProperty;
        this.environment = environment;
    }

    public static TiHttpLog logInfo() {
        return logTheadLocal.get();
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        Map<String, Object> reqParamsAllMap = null;
        if (request instanceof StandardMultipartHttpServletRequest sr) {
            reqParamsAllMap = new HashMap<>();
            request = sr.getRequest();
            MultiValueMap<String, MultipartFile> multiFileMap = sr.getMultiFileMap();
            Map<String, Object> finalParamsMap = reqParamsAllMap;
            multiFileMap.forEach((x, y) -> {
                String collect = y.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.joining(","));
                finalParamsMap.put(x, collect);
            });
        }
        if (!(request instanceof TiRequestWrapper tiRequestWrapper) || !(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        long millis = SystemClock.now();
        String type = request.getMethod();
        String url = request.getRequestURI();
        if (reqParamsAllMap == null) {
            reqParamsAllMap = new HashMap<>();
        }
        // params
        Map<String, String> reqParamsMap = getParams(request);
        reqParamsAllMap.putAll(reqParamsMap);
        String params = toJson(reqParamsAllMap);
        // reqBody
        String reqBody = tiRequestWrapper.getBody();
        // header
        Map<String, String> headersMap = getHeaders(request);
        String reqHeaders = toJson(headersMap);
        String userAgentHeader = request.getHeader(USER_AGENT);
        UserAgent userAgent = UserAgentUtil.parse(userAgentHeader);
        Principal principal = request.getUserPrincipal();
        String port = environment.getProperty("server.port");
        TiLog annotation = handlerMethod.getMethodAnnotation(TiLog.class);
        String name = Optional.ofNullable(annotation).map(TiLog::value).orElse(null);
        String position = handlerMethod.getMethod().getDeclaringClass().getName() + "." + handlerMethod.getMethod().getName() + "()";
        TiHttpLog TIHttpLog = TiHttpLog.builder()
            .type(type)
            .name(name)
            .position(position)
            .ip(IpUtil.getIp(request))
            .url(url)
            .port(port)
            .reqParams(params)
            .reqBody(reqBody)
            .reqHeaders(reqHeaders)
            .start(millis)
            .username((principal != null ? principal.getName() : null))
            .userAgent(userAgent)
            .mdcMap(MDC.getCopyOfContextMap())
            .build();
        logTheadLocal.set(TIHttpLog);
        boolean print = Boolean.TRUE.equals(tiLogProperty.getPrint());
        List<String> antPatterns = tiLogProperty.getAntPatterns();
        boolean anyMatch = antPatterns.stream().anyMatch(x -> antPathMatcher.match(x, url));
        antPathMatchLocal.set(anyMatch);
        if (print && !anyMatch) {
            log.info("[REQ] {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", type, url, nullOfDefault(params), nullOfDefault(reqBody), nullOfDefault(reqHeaders));
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        TiHttpLog tiHttpLog = logTheadLocal.get();
        if (tiHttpLog == null) {
            return;
        }
        String type = request.getMethod();
        String url = request.getRequestURI();
        String resBody = getResBody(response);
        Map<String, String> resHeaderMap = getHeaders(response);
        String resHeaders = toJson(resHeaderMap);
        long end = SystemClock.now();
        int status = response.getStatus();
        Long consume = tiHttpLog.getConsume();
        tiHttpLog.setResBody(resBody);
        tiHttpLog.setResHeaders(resHeaders);
        if (ex != null) {
            tiHttpLog.setErrMessage(ex.getMessage());
        }
        tiHttpLog.setEnd(end);
        tiHttpLog.setStatus(status);
        tiHttpLog.setMdcMap(MDC.getCopyOfContextMap());
        boolean print = Boolean.TRUE.equals(tiLogProperty.getPrint());
        Boolean anyMatch = antPathMatchLocal.get();
        if (print && !anyMatch) {
            log.info("[REQ] {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}, 响应头={}", type, url, status, consume, nullOfDefault(resBody), nullOfDefault(resHeaders));
        }
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        applicationContext.publishEvent(new TiWebLogEvent(applicationContext, tiHttpLog, handler));
        logTheadLocal.remove();
        antPathMatchLocal.remove();
    }

    private String getResBody(HttpServletResponse response) {
        String contentType = response.getContentType();
        boolean flag = contentType != null && (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) ||
            contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        if (!flag) {
            return null;
        }
        TiResponseWrapper tiResponseWrapper = (TiResponseWrapper) response;
        return tiResponseWrapper.getBody();
    }

    public Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> parameteNames = request.getParameterNames();
        while (parameteNames.hasMoreElements()) {
            // 获得每个文本域的name
            String name = parameteNames.nextElement();
            // 根据文本域的name来获取值
            // 因为无法判断文本域是否是单值或者双值，所以我们全部使用双值接收
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
            // 获得每个文本域的name
            String name = headerNames.nextElement();
            // 根据文本域的name来获取值
            // 因为无法判断文本域是否是单值或者双值，所以我们全部使用双值接收
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

    private String toJson(Map<String, ?> map) {
        if (MapUtil.isEmpty(map)) {
            return null;
        }
        return TiJsonUtil.toJsonString(map);
    }

    private String nullOfDefault(String result) {
        if (result == null) {
            return StrUtil.EMPTY;
        }
        return result;
    }

    @Override
    public int getOrder() {
        return tiLogProperty.getOrder();
    }

}