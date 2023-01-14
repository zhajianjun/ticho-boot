package com.ticho.boot.http.interceptor;

import cn.hutool.core.util.StrUtil;
import com.ticho.boot.http.prop.TichoHttpProperty;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
public class OkHttpLogInterceptor implements Interceptor {

    public static final String NONE = "NONE";

    private final TichoHttpProperty tichoHttpProperty;

    public OkHttpLogInterceptor(TichoHttpProperty tichoHttpProperty) {
        this.tichoHttpProperty = tichoHttpProperty;
    }

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        // @formatter:of
        Request req = chain.request();
        // 如果不打印日志，则直接返回
        if (!Boolean.TRUE.equals(tichoHttpProperty.getPrintLog())) {
            return chain.proceed(req);
        }
        String requestPrefixText = tichoHttpProperty.getRequestPrefixText();
        long t1 = System.currentTimeMillis();
        String reqBody = Optional.ofNullable(req.body()).map(Object::toString).orElse(NONE);
        Map<String,List<String>> headersMap = req.headers().toMultimap();
        Map<String, Object> headers = new HashMap<>();
        headersMap.forEach((k,v) -> headers.put(k, String.join(",",v)));
        String method = req.method();
        HttpUrl httpUrl = req.url();
        String url = StrUtil.subBefore(httpUrl.toString(), "?", false);
        Map<String, Object> params = getParams(httpUrl);
        log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", requestPrefixText, method, url, params, reqBody, headers);
        Response res = chain.proceed(req);
        long t2 = System.currentTimeMillis();
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        int byteCount = 1024 * 1024;
        ResponseBody body = res.peekBody(byteCount);
        String resBody = body.string();
        int status = res.code();
        log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}", requestPrefixText, method, url, status, t2-t1, resBody);
        return res;
        // @formatter:on
    }

    public Map<String, Object> getParams(HttpUrl httpUrl) {
        Map<String, Object> map = new HashMap<>();
        Set<String> parameterNames = httpUrl.queryParameterNames();
        for (String parameterName : parameterNames) {
            List<String> values = httpUrl.queryParameterValues(parameterName);
            map.put(parameterName, values.stream().filter(StrUtil::isNotBlank).collect(Collectors.joining(",")));
        }
        return map;
    }

}
