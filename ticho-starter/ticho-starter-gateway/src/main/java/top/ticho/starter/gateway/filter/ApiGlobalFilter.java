package top.ticho.starter.gateway.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.ticho.starter.view.log.TiHttpLog;
import top.ticho.starter.view.log.TiLogProperty;
import top.ticho.tool.json.util.TiJsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 日志过滤
 *
 * @author zhajianjun
 * @date 2023-04-03 11:05
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "ticho.log.enable", havingValue = "true", matchIfMissing = true)
public class ApiGlobalFilter implements GlobalFilter, Ordered {
    public static final String USER_AGENT = "User-Agent";
    /** 日志线程变量 */
    private final TransmittableThreadLocal<TiHttpLog> theadLocal = new TransmittableThreadLocal<>();
    /** 日志配置 */
    private final TiLogProperty tiLogProperty;
    /** 环境变量 */
    private final Environment environment;

    public ApiGlobalFilter(TiLogProperty tiLogProperty, Environment environment) {
        this.environment = environment;
        this.tiLogProperty = tiLogProperty;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        TiHttpLog TiHttpLogInfo = new TiHttpLog();
        return chain
            .filter(preHandle(exchange, TiHttpLogInfo))
            .doFinally(signalType -> complete(TiHttpLogInfo));
    }

    public ServerWebExchange preHandle(ServerWebExchange exchange, TiHttpLog TiHttpLogInfo) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String params = TiJsonUtil.toJsonString(queryParams);
        String type = request.getMethodValue();
        String url = request.getPath().toString();
        String header = headers.getFirst(USER_AGENT);
        UserAgent userAgent = UserAgentUtil.parse(header);
        TiHttpLogInfo.setUrl(url);
        TiHttpLogInfo.setPort(environment.getProperty("server.port"));
        TiHttpLogInfo.setStart(SystemClock.now());
        TiHttpLogInfo.setType(type);
        TiHttpLogInfo.setReqParams(params);
        TiHttpLogInfo.setUserAgent(userAgent);
        TiHttpLogInfo.setMdcMap(MDC.getCopyOfContextMap());
        boolean print = Boolean.TRUE.equals(tiLogProperty.getPrint());
        if (print) {
            String reqBody = TiHttpLogInfo.getReqBody();
            log.info("[REQ] {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", type, url, params, reqBody, headers);
        }
        ServerHttpResponse response = getResponse(exchange, TiHttpLogInfo);
        return exchange.mutate().request(request).response(response).build();
    }

    public ServerHttpResponse getResponse(ServerWebExchange exchange, TiHttpLog TiHttpLogInfo) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            @NonNull
            public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                HttpStatus statusCode = getStatusCode();
                if (Objects.equals(statusCode, HttpStatus.OK) && body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);
                        String resBody = new String(content, StandardCharsets.UTF_8);
                        TiHttpLogInfo.setEnd(SystemClock.now());
                        TiHttpLogInfo.setResBody(resBody);
                        TiHttpLogInfo.setStatus(statusCode.value());
                        originalResponse.getHeaders().setContentLength(content.length);
                        return bufferFactory.wrap(content);
                    }));
                } else {
                    log.error("获取响应体数据 ：{}", statusCode);
                }
                return super.writeWith(body);
            }

            @Override
            @NonNull
            public Mono<Void> writeAndFlushWith(@NonNull Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
    }

    private void complete(TiHttpLog TiHttpLogInfo) {
        boolean print = Boolean.TRUE.equals(tiLogProperty.getPrint());
        String type = TiHttpLogInfo.getType();
        String url = TiHttpLogInfo.getUrl();
        Long consume = TiHttpLogInfo.getConsume();
        Integer status = TiHttpLogInfo.getStatus();
        String resBody = TiHttpLogInfo.getResBody();
        if (print) {
            log.info("[REQ] {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", type, url, status, consume, resBody);
        }
        theadLocal.remove();
    }

    @Override
    public int getOrder() {
        return tiLogProperty.getOrder();
    }


}
