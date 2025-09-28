package top.ticho.trace.spring.interceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ticho.trace.common.TiHttpTraceTag;
import top.ticho.trace.common.TiTraceConst;
import top.ticho.trace.common.TiTraceContext;
import top.ticho.trace.common.TiTraceProperty;
import top.ticho.trace.common.TiTraceReporter;
import top.ticho.trace.spring.util.TiIpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    private final TiTraceReporter tiTraceReporter;
    /** url地址匹配 */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public TiTraceInterceptor(TiTraceProperty tiTraceProperty, Environment environment, TiTraceReporter tiTraceReporter) {
        this.tiTraceProperty = tiTraceProperty;
        this.environment = environment;
        this.tiTraceReporter = tiTraceReporter;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String methodName = "";
        if (handler instanceof HandlerMethod handlerMethod) {
            methodName = handlerMethod.toString();
        }
        TiTraceContext.init(tiTraceReporter);
        String trace = tiTraceProperty.getTrace();
        String appName = environment.getProperty("spring.application.name");
        String traceId = request.getHeader(TiTraceConst.TRACE_ID_KEY);
        String parentSpanId = request.getHeader(TiTraceConst.SPAN_ID_KEY);
        TiTraceContext.start(appName, traceId, parentSpanId, trace);
        TiTraceContext.addTag(TiHttpTraceTag.IP, TiIpUtil.getIp(request));
        TiTraceContext.addTag(TiHttpTraceTag.ENV, environment.getProperty("spring.profiles.active"));
        TiTraceContext.addTag(TiHttpTraceTag.URL, request.getRequestURI());
        TiTraceContext.addTag(TiHttpTraceTag.METHOD, methodName);
        TiTraceContext.addTag(TiHttpTraceTag.TYPE, request.getMethod());
        // response添加traceId和spanId
        response.setHeader(TiTraceConst.TRACE_ID_KEY, TiTraceContext.getTraceId());
        response.setHeader(TiTraceConst.PARENT_SPAN_ID_KEY, parentSpanId);
        response.setHeader(TiTraceConst.SPAN_ID_KEY, TiTraceContext.getSpanId());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        TiTraceContext.addTag(TiHttpTraceTag.STATUS, String.valueOf(response.getStatus()));
        String url = TiTraceContext.getTag(TiHttpTraceTag.URL);
        boolean ignore = tiTraceProperty.getAntPatterns().stream().anyMatch(x -> antPathMatcher.match(x, url));
        TiTraceContext.close(!ignore);
    }

    @Override
    public int getOrder() {
        return tiTraceProperty.getOrder();
    }

}
