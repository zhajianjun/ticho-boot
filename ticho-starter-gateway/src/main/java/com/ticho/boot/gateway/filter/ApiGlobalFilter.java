package com.ticho.boot.gateway.filter;

import cn.hutool.core.date.SystemClock;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.view.log.BaseLogProperty;
import com.ticho.boot.view.log.HttpLog;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
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
    private final TransmittableThreadLocal<HttpLog> theadLocal = new TransmittableThreadLocal<>();
    /** 日志配置 */
    private final BaseLogProperty baseLogProperty;
    /** 环境变量 */
    private final Environment environment;

    public ApiGlobalFilter(BaseLogProperty baseLogProperty, Environment environment) {
        this.environment = environment;
        this.baseLogProperty = baseLogProperty;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // @formatter:off
        HttpLog httpLogInfo = new HttpLog();
        return chain.filter(preHandle(exchange, httpLogInfo))
                .doFinally(signalType -> complete(httpLogInfo));
        // @formatter:on
    }

    public ServerWebExchange preHandle(ServerWebExchange exchange, HttpLog httpLogInfo) {
        // @formatter:off
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String params = JsonUtil.toJsonString(queryParams);
        String type = request.getMethodValue();
        String url = request.getPath().toString();
        String header = headers.getFirst(USER_AGENT);
        UserAgent userAgent =  UserAgentUtil.parse(header);
        httpLogInfo.setUrl(url);
        httpLogInfo.setPort(environment.getProperty("server.port"));
        httpLogInfo.setStart(SystemClock.now());
        httpLogInfo.setType(type);
        httpLogInfo.setReqParams(params);
        httpLogInfo.setUserAgent(userAgent);
        boolean print = Boolean.TRUE.equals(baseLogProperty.getPrint());
        String requestPrefixText = baseLogProperty.getReqPrefix();
        if (print) {
            String reqBody = httpLogInfo.getReqBody();
            log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", requestPrefixText, type, url, params, reqBody, headers);
        }
        ServerHttpResponse response = getResponse(exchange, httpLogInfo);
        return exchange.mutate().request(request).response(response).build();
        // @formatter:on
    }

    public ServerHttpResponse getResponse(ServerWebExchange exchange, HttpLog httpLogInfo) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
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
                        httpLogInfo.setEnd(SystemClock.now());
                        httpLogInfo.setResBody(resBody);
                        httpLogInfo.setStatus(statusCode.value());
                        originalResponse.getHeaders().setContentLength(content.length);
                        return bufferFactory.wrap(content);
                    }));
                } else {
                    log.error("获取响应体数据 ：" + statusCode);
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

    private void complete(HttpLog httpLogInfo) {
        boolean print = Boolean.TRUE.equals(baseLogProperty.getPrint());
        String requestPrefixText = baseLogProperty.getReqPrefix();
        String type = httpLogInfo.getType();
        String url = httpLogInfo.getUrl();
        Long consume = httpLogInfo.getConsume();
        Integer status = httpLogInfo.getStatus();
        String resBody = httpLogInfo.getResBody();
        if (print) {
            log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", requestPrefixText, type, url, status, consume, resBody);
        }
        theadLocal.remove();
    }

    @Override
    public int getOrder() {
        return baseLogProperty.getOrder();
    }


}
