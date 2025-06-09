package top.ticho.trace.okhttp.interceptor;

import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.MDC;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.core.util.TiTraceUtil;

import java.io.IOException;

/**
 * okhttp3链路追踪传递
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class OkHttpLogInterceptor implements Interceptor {

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String traceId = MDC.get(LogConst.TRACE_ID_KEY);
        if (StrUtil.isBlank(traceId)) {
            if (log.isDebugEnabled()) {
                log.debug("MDC中不存在链路信息,本次调用不传递traceId");
            }
            return chain.proceed(req);
        }
        Request.Builder builder = req.newBuilder();
        builder.addHeader(LogConst.TRACE_ID_KEY, traceId);
        builder.addHeader(LogConst.SPAN_ID_KEY, TiTraceUtil.nextSpanId());
        builder.addHeader(LogConst.PRE_APP_NAME_KEY, MDC.get(LogConst.APP_NAME_KEY));
        builder.addHeader(LogConst.PRE_IP_KEY, MDC.get(LogConst.IP_KEY));
        req = builder.build();
        return chain.proceed(req);
    }

}
