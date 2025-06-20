package top.ticho.trace.gateway.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.ticho.tool.json.util.TiJsonUtil;
import top.ticho.trace.common.bean.HttpLogInfo;
import top.ticho.trace.common.bean.TraceInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.common.prop.TiTraceProperty;
import top.ticho.trace.core.handle.TracePushContext;
import top.ticho.trace.core.util.TiTraceUtil;

import jakarta.annotation.Nonnull;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 链路全局过滤
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class TraceGlobalFilter implements GlobalFilter, Ordered {
    /** 本地ip列表 */
    private static final List<String> localhosts = Stream.of("127.0.0.1", "0:0:0:0:0:0:0:1").collect(Collectors.toList());
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
        HttpLogInfo httpLogInfo = new HttpLogInfo();
        return chain.filter(preHandle(exchange, httpLogInfo))
            .doFinally(signalType -> complete(httpLogInfo));
    }

    public ServerWebExchange preHandle(ServerWebExchange exchange, HttpLogInfo httpLogInfo) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpHeaders headers = serverHttpRequest.getHeaders();
        String traceId = headers.getFirst(LogConst.TRACE_ID_KEY);
        String spanId = headers.getFirst(LogConst.SPAN_ID_KEY);
        String preAppName = headers.getFirst(LogConst.PRE_APP_NAME_KEY);
        String preIp = headers.getFirst(LogConst.PRE_IP_KEY);
        if (preIp == null) {
            preIp = preIp(serverHttpRequest);
        }
        MultiValueMap<String, String> queryParams = serverHttpRequest.getQueryParams();
        String params = TiJsonUtil.toJsonString(queryParams);
        String ip = localIp();
        String appName = environment.getProperty("spring.application.name");
        String trace = tiTraceProperty.getTrace();
        String type = serverHttpRequest.getMethod().name();
        String url = serverHttpRequest.getPath().toString();
        httpLogInfo.setUrl(url);
        httpLogInfo.setPort(environment.getProperty("server.port"));
        httpLogInfo.setStart(SystemClock.now());
        httpLogInfo.setType(type);
        httpLogInfo.setReqParams(params);
        TiTraceUtil.prepare(traceId, spanId, appName, ip, preAppName, preIp, trace);
        traceId = MDC.get(LogConst.TRACE_ID_KEY);
        String finalTraceId = traceId;
        Consumer<HttpHeaders> httpHeaders = httpHeader -> {
            httpHeader.set(LogConst.TRACE_ID_KEY, finalTraceId);
            httpHeader.set(LogConst.SPAN_ID_KEY, TiTraceUtil.nextSpanId());
            httpHeader.set(LogConst.PRE_APP_NAME_KEY, appName);
            httpHeader.set(LogConst.PRE_IP_KEY, ip);
        };
        ServerHttpRequest newRequest = serverHttpRequest.mutate().headers(httpHeaders).build();
        ServerHttpResponse response = getResponse(exchange, httpLogInfo);
        return exchange.mutate().request(newRequest).response(response).build();
    }

    private void complete(HttpLogInfo httpLogInfo) {
        TraceInfo traceInfo = TraceInfo.builder()
            .traceId(MDC.get(LogConst.TRACE_ID_KEY))
            .spanId(MDC.get(LogConst.SPAN_ID_KEY))
            .appName(MDC.get(LogConst.APP_NAME_KEY))
            .ip(MDC.get(LogConst.IP_KEY))
            .preAppName(MDC.get(LogConst.PRE_APP_NAME_KEY))
            .preIp(MDC.get(LogConst.PRE_IP_KEY))
            .url(httpLogInfo.getUrl())
            .port(httpLogInfo.getPort())
            .method("")
            .type(httpLogInfo.getType())
            .status(httpLogInfo.getStatus())
            .start(httpLogInfo.getStart())
            .end(httpLogInfo.getEnd())
            .consume(httpLogInfo.getConsume())
            .build();
        TiTraceUtil.complete();
        TracePushContext.asyncPushTrace(tiTraceProperty, traceInfo);
    }

    public ServerHttpResponse getResponse(ServerWebExchange exchange, HttpLogInfo httpLogInfo) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            @Nonnull
            public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                HttpStatusCode httpStatusCode = getStatusCode();
                // 响应状态码
                httpLogInfo.setStatus(httpStatusCode.value());
                if (Objects.equals(httpStatusCode, HttpStatus.OK) && body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);
                        // String responseData = new String(content, StandardCharsets.UTF_8);
                        // httpLogInfo.setResBody(responseData);
                        originalResponse.getHeaders().setContentLength(content.length);
                        return bufferFactory.wrap(content);
                    }));
                } else {
                    log.error("获取响应体数据 ：" + httpStatusCode);
                }
                httpLogInfo.setEnd(SystemClock.now());
                return super.writeWith(body);
            }

            @Override
            @NonNull
            public Mono<Void> writeAndFlushWith(@NonNull Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
    }

    public static String preIp(ServerHttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        String ip = httpHeaders.getFirst("x-forwarded-for");
        // Proxy-Client-IP 这个一般是经过apache http服务器的请求才会有，用apache http做代理时一般会加上Proxy-Client-IP请求头，而WL-Proxy-Client-IP是他的weblogic插件加上的头。
        String unknown = "unknown";
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            ip = httpHeaders.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            ip = Optional.ofNullable(request.getRemoteAddress())
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .orElse(null);
        }
        if (StrUtil.isBlank(ip)) {
            return "";
        }
        int index = ip.indexOf(",");
        if (index != -1) {
            return ip.substring(0, index);
        }
        if (!localhosts.contains(ip)) {
            return ip;
        }
        // 获取本机真正的ip地址
        return localIp();
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
