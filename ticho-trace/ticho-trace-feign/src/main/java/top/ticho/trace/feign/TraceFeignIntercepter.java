package top.ticho.trace.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.ticho.trace.common.TiTraceConst;
import top.ticho.trace.common.TiTraceContext;

/**
 * 链路feign拦截器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@Component
public class TraceFeignIntercepter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(TiTraceConst.TRACE_ID_KEY, TiTraceContext.getTraceId());
        requestTemplate.header(TiTraceConst.SPAN_ID_KEY, TiTraceContext.getSpanId());
    }

}