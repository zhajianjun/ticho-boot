package top.ticho.trace.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.ticho.trace.common.TiTraceConst;
import top.ticho.trace.common.TiTraceContext;
import top.ticho.trace.common.TiTraceProperty;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

/**
 * 链路全局过滤
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class TraceGlobalFilter implements GlobalFilter, Ordered {
    /** 环境变量 */
    private final Environment environment;
    /** 链路配置 */
    private final TiTraceProperty tiTraceProperty;

    public TraceGlobalFilter(TiTraceProperty tiTraceProperty, Environment environment) {
        this.environment = environment;
        this.tiTraceProperty = tiTraceProperty;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain
            .filter(preHandle(exchange))
            .doFinally(signalType -> TiTraceContext.close());
    }

    public ServerWebExchange preHandle(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpHeaders headers = serverHttpRequest.getHeaders();
        String traceId = headers.getFirst(TiTraceConst.TRACE_ID_KEY);
        String spanId = headers.getFirst(TiTraceConst.SPAN_ID_KEY);
        String appName = environment.getProperty("spring.application.name");
        TiTraceContext.start(appName, traceId, spanId, tiTraceProperty.getTrace());
        TiTraceContext.addTag("ip", localIp());
        TiTraceContext.addTag("env", environment.getProperty("spring.profiles.active"));
        TiTraceContext.addTag("url", serverHttpRequest.getPath().toString());
        TiTraceContext.addTag("type", serverHttpRequest.getMethod().name());
        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
            httpHeader.set(TiTraceConst.TRACE_ID_KEY, traceId);
            httpHeader.set(TiTraceConst.SPAN_ID_KEY, spanId);
        };
        ServerHttpRequest newRequest = serverHttpRequest.mutate().headers(httpHeaders).build();
        return exchange.mutate().request(newRequest).build();
    }

    public static String localIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return tiTraceProperty.getOrder();
    }

}
