package top.ticho.trace.feign;

import cn.hutool.core.util.StrUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.core.util.TiTraceUtil;

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
        String traceId = MDC.get(LogConst.TRACE_ID_KEY);
        if (StrUtil.isBlank(traceId)) {
            log.debug("MDC中不存在链路信息,本次调用不传递traceId");
            return;
        }
        requestTemplate.header(LogConst.TRACE_ID_KEY, traceId);
        requestTemplate.header(LogConst.SPAN_ID_KEY, TiTraceUtil.nextSpanId());
        requestTemplate.header(LogConst.PRE_APP_NAME_KEY, MDC.get(LogConst.APP_NAME_KEY));
        requestTemplate.header(LogConst.PRE_IP_KEY, MDC.get(LogConst.IP_KEY));
    }

}