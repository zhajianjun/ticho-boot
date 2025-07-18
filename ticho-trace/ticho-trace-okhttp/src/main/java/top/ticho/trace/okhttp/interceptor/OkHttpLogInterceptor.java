package top.ticho.trace.okhttp.interceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import top.ticho.trace.common.TiTraceConst;
import top.ticho.trace.common.TiTraceContext;

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
        TiTraceContext.startSpan(req.url().host());
        Request.Builder builder = req.newBuilder();
        builder.addHeader(TiTraceConst.TRACE_ID_KEY, TiTraceContext.getTraceId());
        builder.addHeader(TiTraceConst.SPAN_ID_KEY, TiTraceContext.getSpanId());
        req = builder.build();
        Response proceed = chain.proceed(req);
        TiTraceContext.closeSpan();
        return proceed;
    }

}
