package com.ticho.boot.http.interceptor;

import com.ticho.boot.http.prop.TichoHttpProperty;
import com.ticho.boot.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
public class OkHttpLogInterceptor implements Interceptor {

    public static final String NONE = "NONE";

    private static final String requestPrefixText = "[HTTP]";

    private final TichoHttpProperty tichoHttpProperty;

    public OkHttpLogInterceptor(TichoHttpProperty tichoHttpProperty) {
        this.tichoHttpProperty = tichoHttpProperty;
    }

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        // @formatter:off
        Request req = chain.request();
        // 如果不打印日志，则直接返回
        if (!Boolean.TRUE.equals(tichoHttpProperty.getPrintLog())) {
            return chain.proceed(req);
        }
        long t1 = System.currentTimeMillis();
        String reqBody = Optional.ofNullable(req.body()).map(Object::toString).orElse(NONE);
        Map<String,List<String>> headersMap = req.headers().toMultimap();
        Map<String, Object> headers = new HashMap<>();
        headersMap.forEach((k,v) -> headers.put(k, String.join(",",v)));
        log.info("{} 请求地址: {} {}", requestPrefixText,  req.method(), req.url());
        log.info("{} 请求体: {}", requestPrefixText, reqBody);
        log.info("{} 请求头: {}", requestPrefixText, JsonUtil.toJsonString(headers));
        Response res = chain.proceed(req);
        long t2 = System.currentTimeMillis();
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        int byteCount = 1024 * 1024;
        ResponseBody body = res.peekBody(byteCount);
        String resBody = body.string();
        log.info("{} 响应体: {}", requestPrefixText, resBody);
        log.info("{} 耗时: {}ms", requestPrefixText, t2 - t1);
        log.info("{} 日志结束", requestPrefixText);
        return res;
        // @formatter:on
    }

}
