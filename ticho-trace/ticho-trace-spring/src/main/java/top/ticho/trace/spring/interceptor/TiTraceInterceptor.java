package top.ticho.trace.spring.interceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ticho.trace.common.TiReporter;
import top.ticho.trace.common.TiTraceConst;
import top.ticho.trace.common.TiTraceContext;
import top.ticho.trace.common.TiTraceProperty;
import top.ticho.trace.spring.util.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 链路拦截器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class TiTraceInterceptor implements HandlerInterceptor, Ordered {

    /** 链路配置 */
    private final TiTraceProperty tiTraceProperty;
    /** 环境变量 */
    private final Environment environment;
    /** 链路报告器 */
    private final TiReporter tiReporter;

    public TiTraceInterceptor(TiTraceProperty tiTraceProperty, Environment environment, TiReporter tiReporter) {
        this.tiTraceProperty = tiTraceProperty;
        this.environment = environment;
        this.tiReporter = tiReporter;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        TiTraceContext.init(tiReporter);
        Map<String, String> headersMap = getHeaders(request);
        String trace = tiTraceProperty.getTrace();
        String appName = environment.getProperty("spring.application.name");
        String traceId = headersMap.get(TiTraceConst.TRACE_ID_KEY);
        String spanId = headersMap.get(TiTraceConst.SPAN_ID_KEY);
        TiTraceContext.start(appName, traceId, spanId, trace);
        TiTraceContext.addTag("ip", IpUtil.localIp());
        TiTraceContext.addTag("env", environment.getProperty("spring.profiles.active"));
        TiTraceContext.addTag("url", request.getRequestURI());
        TiTraceContext.addTag("method", handlerMethod.toString());
        TiTraceContext.addTag("type", request.getMethod());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        TiTraceContext.addTag("status", String.valueOf(response.getStatus()));
        TiTraceContext.close();
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

    @Override
    public int getOrder() {
        return tiTraceProperty.getOrder();
    }

}
