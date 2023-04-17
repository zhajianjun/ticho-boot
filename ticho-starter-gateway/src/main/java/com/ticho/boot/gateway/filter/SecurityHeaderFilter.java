package com.ticho.boot.gateway.filter;

import com.ticho.boot.gateway.constant.CommonConst;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 权限请求头处理
 *
 * @author zhajianjun
 * @date 2021-10-18 22:57
 */
@Component
public class SecurityHeaderFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            // 网关调用的接口都是外部接口，强制更改inner=false
            ServerHttpRequest newRequest = request.mutate().header(CommonConst.INNER, CommonConst.INNER_VALUE_FALSE).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        };
    }
}
