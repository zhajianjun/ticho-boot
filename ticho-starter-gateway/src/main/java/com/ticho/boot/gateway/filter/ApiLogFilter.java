package com.ticho.boot.gateway.filter;

import com.ticho.boot.gateway.prop.BaseGatewayProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * api日志网关过滤
 *
 * @author zhajianjun
 * @date 2023-01-06 10:10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogFilter implements GlobalFilter, Ordered {

    private final BaseGatewayProperty baseGatewayProperty;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (!Boolean.TRUE.equals(baseGatewayProperty.getPrint())) {
            return chain.filter(exchange);
        }
        log.info("请求路径：{}", request.getURI());
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

}
